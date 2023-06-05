package io.snaps.basefeed.data

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import io.snaps.basefeed.data.model.AddVideoRequestDto
import io.snaps.basefeed.data.model.LikedVideoFeedItemResponseDto
import io.snaps.basefeed.domain.VideoFeedPageModel
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.basefeed.domain.VideoClipModel
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.database.UserDataStorage
import io.snaps.coredata.network.PagedLoader
import io.snaps.coredata.network.PagedLoaderParams
import io.snaps.coredata.network.apiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import java.time.ZoneOffset
import javax.inject.Inject

interface VideoFeedRepository {

    fun getFeedState(feedType: VideoFeedType): StateFlow<VideoFeedPageModel>

    suspend fun refreshFeed(feedType: VideoFeedType): Effect<Completable>

    suspend fun loadNextFeedPage(feedType: VideoFeedType): Effect<Completable>

    /**
     * returns: upload id to track the progress
     */
    suspend fun upload(title: String, fileId: Uuid, file: String): Effect<Uuid>

    suspend fun delete(videoId: Uuid): Effect<Completable>

    suspend fun like(videoId: Uuid): Effect<Completable>

    suspend fun markShown(videoId: Uuid): Effect<Completable>

    suspend fun markWatched(videoId: Uuid): Effect<Completable>

    // todo delete once checked on backend
    fun isAllowedToCreate(userInfoModel: UserInfoModel?): Pair<Boolean, Int>

    // todo delete once checked on backend
    fun onVideoCreated(userInfoModel: UserInfoModel?)
}

class VideoFeedRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val videoFeedApi: VideoFeedApi,
    private val loaderFactory: VideoFeedLoaderFactory,
    private val videoFeedUploader: VideoFeedUploader,
    private val userDataStorage: UserDataStorage,
) : VideoFeedRepository {

    private var _likedVideos: List<LikedVideoFeedItemResponseDto>? = null

    private suspend fun getLikedVideos(): List<LikedVideoFeedItemResponseDto> {
        return _likedVideos ?: apiCall(ioDispatcher) {
            // todo better way once back supports
            videoFeedApi.myLikedFeed(null, 1000)
        }.doOnSuccess {
            _likedVideos = it
        }.dataOrCache ?: emptyList()
    }

    private fun getLoader(videoFeedType: VideoFeedType): PagedLoader<*, VideoClipModel> {
        return loaderFactory.get(videoFeedType) { type ->
            when (type) {
                VideoFeedType.Main -> PagedLoaderParams(
                    action = { from, count -> videoFeedApi.feed(from = from, count = count) },
                    pageSize = 20,
                    nextPageIdFactory = { it.entityId },
                    mapper = { it.toVideoClipModelList(likedVideos = getLikedVideos()) },
                )
                VideoFeedType.Subscriptions -> PagedLoaderParams(
                    action = { from, count -> videoFeedApi.subscriptionFeed(from = from, count = count) },
                    pageSize = 5,
                    nextPageIdFactory = { it.entityId },
                    mapper = { it.toVideoClipModelList(likedVideos = getLikedVideos()) },
                )
                is VideoFeedType.Single -> PagedLoaderParams(
                    action = { _, _ -> videoFeedApi.feed(from = type.videoId, count = 1) },
                    pageSize = 1,
                    nextPageIdFactory = { null },
                    mapper = { it.toVideoClipModelList(likedVideos = getLikedVideos()) },
                )
                is VideoFeedType.Popular -> PagedLoaderParams(
                    action = { from, count -> videoFeedApi.popularFeed(from = from, count = count) },
                    pageSize = 50,
                    nextPageIdFactory = { it.entityId },
                    mapper = { it.toVideoClipModelList(likedVideos = getLikedVideos()) },
                )
                is VideoFeedType.User -> PagedLoaderParams(
                    action = { from, count ->
                        if (type.userId != null) {
                            videoFeedApi.userFeed(userId = type.userId, from = from, count = count)
                        } else {
                            videoFeedApi.myFeed(from = from, count = count)
                        }
                    },
                    pageSize = 50,
                    nextPageIdFactory = { it.entityId },
                    mapper = { it.toVideoClipModelList(likedVideos = getLikedVideos()) },
                )
                is VideoFeedType.Search -> PagedLoaderParams(
                    action = { from, count ->
                        videoFeedApi.searchFeed(query = type.query, from = from, count = count)
                    },
                    pageSize = 50,
                    nextPageIdFactory = { it.entityId },
                    mapper = { it.toVideoClipModelList(likedVideos = getLikedVideos()) },
                )
                is VideoFeedType.Liked -> PagedLoaderParams(
                    action = { from, count ->
                        if (type.userId == null) {
                            videoFeedApi.myLikedFeed(from = from, count = count).toFeedBaseResponse()
                        } else {
                            videoFeedApi.likedFeed(userId = type.userId, from = from, count = count)
                        }
                    },
                    pageSize = 50,
                    nextPageIdFactory = { it.entityId },
                    mapper = {
                        it.toVideoClipModelList(isExplicitlyLiked = type.userId == null, likedVideos = getLikedVideos())
                    },
                )
            }
        }
    }

    override fun getFeedState(feedType: VideoFeedType): StateFlow<VideoFeedPageModel> =
        getLoader(feedType).state

    override suspend fun refreshFeed(feedType: VideoFeedType): Effect<Completable> =
        getLoader(feedType).refresh()

    override suspend fun loadNextFeedPage(feedType: VideoFeedType): Effect<Completable> =
        getLoader(feedType).loadNext()

    override suspend fun like(videoId: Uuid): Effect<Completable> {
        return apiCall(ioDispatcher) { videoFeedApi.like(videoId = videoId) }
    }

    override suspend fun markWatched(videoId: Uuid): Effect<Completable> {
        return apiCall(ioDispatcher) { videoFeedApi.markWatched(videoId = videoId) }
    }

    override suspend fun markShown(videoId: Uuid): Effect<Completable> {
        return apiCall(ioDispatcher) { videoFeedApi.markShown(videoId = videoId) }
    }

    override suspend fun upload(title: String, fileId: Uuid, file: String): Effect<Uuid> {
        return apiCall(ioDispatcher) {
            videoFeedApi.addVideo(AddVideoRequestDto(title = title, description = title, thumbnailFileId = fileId))
        }.flatMap {
            videoFeedUploader.upload(videoId = it.internalId, filePath = file)
        }
    }

    override suspend fun delete(videoId: Uuid): Effect<Completable> {
        return apiCall(ioDispatcher) { videoFeedApi.deleteVideo(videoId = videoId) }
    }

    override fun isAllowedToCreate(userInfoModel: UserInfoModel?): Pair<Boolean, Int> {
        val date = userInfoModel?.questInfo?.questDate?.toInstant(ZoneOffset.UTC)?.toEpochMilli() ?: return true to 0
        val maxCount = kotlin.runCatching {
            // todo central remote data source for fb
            FirebaseRemoteConfig.getInstance().getLong("max_videos_count")
        }.getOrNull() ?: return true to 0
        val currentCount = userDataStorage.getCreatedVideoCount(userInfoModel.userId, date)
        return (currentCount < maxCount) to maxCount.toInt()
    }

    override fun onVideoCreated(userInfoModel: UserInfoModel?) {
        val date = userInfoModel?.questInfo?.questDate?.toInstant(ZoneOffset.UTC)?.toEpochMilli() ?: return
        val currentCount = userDataStorage.getCreatedVideoCount(userInfoModel.userId, date)
        userDataStorage.setCreatedVideoCount(userInfoModel.userId, date, currentCount + 1)
    }
}