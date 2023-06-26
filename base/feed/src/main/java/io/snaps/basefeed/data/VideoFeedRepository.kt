package io.snaps.basefeed.data

import dagger.Lazy
import io.snaps.basefeed.data.model.AddVideoRequestDto
import io.snaps.basefeed.domain.VideoFeedPageModel
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.basefeed.domain.VideoClipModel
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.coroutine.UserSessionCoroutineScope
import io.snaps.coredata.database.UserDataStorage
import io.snaps.coredata.network.PagedLoader
import io.snaps.coredata.network.PagedLoaderParams
import io.snaps.coredata.network.apiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.ZoneOffset
import javax.inject.Inject

interface VideoFeedRepository {

    fun getFeedState(feedType: VideoFeedType): StateFlow<VideoFeedPageModel>

    suspend fun refreshFeed(feedType: VideoFeedType): Effect<Completable>

    suspend fun loadNextFeedPage(feedType: VideoFeedType): Effect<Completable>

    /**
     * returns: upload id to track the progress
     */
    suspend fun upload(title: String, thumbnailFileId: Uuid, file: String, userInfoModel: UserInfoModel?): Effect<Uuid>

    /**
     * returns: upload id to track the progress
     */
    suspend fun retryUpload(videoId: Uuid): Effect<Uuid>

    suspend fun get(videoId: Uuid): Effect<VideoClipModel>

    suspend fun delete(videoId: Uuid): Effect<Completable>

    suspend fun like(videoId: Uuid): Effect<Completable>

    suspend fun markShown(videoId: Uuid): Effect<Completable>

    suspend fun markWatched(videoId: Uuid): Effect<Completable>
}

class VideoFeedRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @UserSessionCoroutineScope private val scope: CoroutineScope,
    private val videoFeedApi: Lazy<VideoFeedApi>,
    private val loaderFactory: VideoFeedLoaderFactory,
    private val uploader: VideoFeedUploader,
    private val userDataStorage: UserDataStorage,
    private val uploadStatusSource: UploadStatusSource,
) : VideoFeedRepository {

    private data class UploadInfo(
        val title: String,
        val thumbnailFileId: Uuid,
        val file: String,
        val userInfoModel: UserInfoModel?,
    )

    private val failedUploads = mutableMapOf<Uuid, UploadInfo>()

    private fun getLoader(videoFeedType: VideoFeedType): PagedLoader<*, VideoClipModel> {
        return loaderFactory.get(videoFeedType) { type ->
            when (type) {
                VideoFeedType.Main -> PagedLoaderParams(
                    action = { from, count -> videoFeedApi.get().getFeed(from = from, count = count) },
                    pageSize = 20,
                    nextPageIdFactory = { it.entityId },
                    mapper = { it.toVideoClipModelList() },
                )
                VideoFeedType.Subscriptions -> PagedLoaderParams(
                    action = { from, count -> videoFeedApi.get().getSubscriptionFeed(from = from, count = count) },
                    pageSize = 5,
                    nextPageIdFactory = { it.entityId },
                    mapper = { it.toVideoClipModelList() },
                )
                is VideoFeedType.Single -> PagedLoaderParams(
                    action = { _, _ -> videoFeedApi.get().getVideo(type.videoId).toFeedBaseResponse() },
                    pageSize = 1,
                    nextPageIdFactory = { null },
                    mapper = { it.toVideoClipModelList() },
                )
                is VideoFeedType.Popular -> PagedLoaderParams(
                    action = { from, count -> videoFeedApi.get().getPopularFeed(from = from, count = count) },
                    pageSize = 50,
                    nextPageIdFactory = { it.entityId },
                    mapper = { it.toVideoClipModelList() },
                )
                is VideoFeedType.User -> PagedLoaderParams(
                    action = { from, count ->
                        if (type.userId != null) {
                            videoFeedApi.get().getUserFeed(userId = type.userId, from = from, count = count)
                        } else {
                            videoFeedApi.get().getMyFeed(from = from, count = count)
                        }
                    },
                    pageSize = 50,
                    nextPageIdFactory = { it.entityId },
                    mapper = { it.toVideoClipModelList() },
                )
                is VideoFeedType.Search -> PagedLoaderParams(
                    action = { from, count ->
                        videoFeedApi.get().getSearchFeed(query = type.query, from = from, count = count)
                    },
                    pageSize = 50,
                    nextPageIdFactory = { it.entityId },
                    mapper = { it.toVideoClipModelList() },
                )
                is VideoFeedType.Liked -> PagedLoaderParams(
                    action = { from, count ->
                        if (type.userId == null) {
                            videoFeedApi.get().getMyLikedFeed(from = from, count = count).toFeedBaseResponse()
                        } else {
                            videoFeedApi.get().getLikedFeed(userId = type.userId, from = from, count = count)
                        }
                    },
                    pageSize = 50,
                    nextPageIdFactory = { it.entityId },
                    mapper = { it.toVideoClipModelList(isExplicitlyLiked = true) },
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

    override suspend fun get(videoId: Uuid): Effect<VideoClipModel> {
        return apiCall(ioDispatcher) { videoFeedApi.get().getVideo(videoId) }.map { it.toModel(false) }
    }

    override suspend fun like(videoId: Uuid): Effect<Completable> {
        return apiCall(ioDispatcher) { videoFeedApi.get().likeVideo(videoId = videoId) }
    }

    override suspend fun markWatched(videoId: Uuid): Effect<Completable> {
        return apiCall(ioDispatcher) { videoFeedApi.get().markVideoWatched(videoId = videoId) }
    }

    override suspend fun markShown(videoId: Uuid): Effect<Completable> {
        return apiCall(ioDispatcher) { videoFeedApi.get().markVideoShown(videoId = videoId) }
    }

    override suspend fun upload(
        title: String,
        thumbnailFileId: Uuid,
        file: String,
        userInfoModel: UserInfoModel?,
    ): Effect<Uuid> {
        return apiCall(ioDispatcher) {
            videoFeedApi.get().addVideo(
                AddVideoRequestDto(
                    title = title,
                    description = title,
                    thumbnailFileId = thumbnailFileId
                )
            )
        }.flatMap { dto ->
            refreshFeed(VideoFeedType.User(null)).map { dto }
        }.flatMap { dto ->
            doUpload(
                videoId = dto.internalId,
                uploadInfo = UploadInfo(
                    title = title,
                    thumbnailFileId = thumbnailFileId,
                    file = file,
                    userInfoModel = userInfoModel,
                )
            )
        }
    }

    private suspend fun doUpload(videoId: Uuid, uploadInfo: UploadInfo): Effect<Uuid> {
        return uploader.upload(videoId = videoId, filePath = uploadInfo.file).doOnSuccess { uploadId ->
            uploadStatusSource.listenToByUploadId(uploadId = uploadId).onEach {
                when (it) {
                    is UploadStatusSource.State.Success -> {
                        // todo delete once checked on backend
                        incrementCreatedVideoCount(userInfoModel = uploadInfo.userInfoModel)
                    }
                    is UploadStatusSource.State.Error -> {
                        failedUploads[videoId] = uploadInfo
                    }
                    is UploadStatusSource.State.Progress -> Unit
                }
            }.launchIn(scope)
        }
    }

    override suspend fun retryUpload(videoId: Uuid): Effect<Uuid> {
        return failedUploads[videoId]?.let {
            doUpload(videoId = videoId, uploadInfo = it)
        } ?: Effect.error(AppError.Unknown())
    }

    private fun incrementCreatedVideoCount(userInfoModel: UserInfoModel?) {
        val date = userInfoModel?.questInfo?.questDate?.toInstant(ZoneOffset.UTC)?.toEpochMilli() ?: return
        val currentCount = userDataStorage.getCreatedVideoCount(userInfoModel.userId, date)
        userDataStorage.setCreatedVideoCount(userInfoModel.userId, date, currentCount + 1)
    }

    override suspend fun delete(videoId: Uuid): Effect<Completable> {
        return apiCall(ioDispatcher) { videoFeedApi.get().deleteVideo(videoId = videoId) }
    }
}