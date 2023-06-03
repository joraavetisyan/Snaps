package io.snaps.basefeed.data

import io.snaps.basefeed.data.model.AddVideoRequestDto
import io.snaps.basefeed.data.model.UserLikedVideoResponseDto
import io.snaps.basefeed.domain.VideoFeedPageModel
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.basefeed.domain.VideoClipModel
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.PagedLoader
import io.snaps.coredata.network.PagedLoaderParams
import io.snaps.coredata.network.apiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface VideoFeedRepository {

    fun getFeedState(feedType: VideoFeedType): StateFlow<VideoFeedPageModel>

    suspend fun refreshFeed(feedType: VideoFeedType): Effect<Completable>

    suspend fun loadNextFeedPage(feedType: VideoFeedType): Effect<Completable>

    suspend fun markWatched(videoId: Uuid): Effect<Completable>

    suspend fun like(videoId: Uuid): Effect<Completable>

    suspend fun uploadVideo(
        title: String,
        fileId: Uuid,
        file: String,
    ): Effect<Uuid>

    suspend fun deleteVideo(videoId: Uuid): Effect<Completable>

    suspend fun markShowed(videoId: Uuid): Effect<Completable>
}

class VideoFeedRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val videoFeedApi: VideoFeedApi,
    private val loaderFactory: VideoFeedLoaderFactory,
    private val likedFeedLoaderFactory: UserLikedVideoFeedLoaderFactory,
    private val videFeedUploader: VideFeedUploader,
) : VideoFeedRepository {

    private var likedVideos: List<UserLikedVideoResponseDto>? = null

    private fun getLoader(videoFeedType: VideoFeedType): PagedLoader<*, VideoClipModel> {
        return when (videoFeedType) {
            is VideoFeedType.Liked -> when (videoFeedType.userId) {
                null -> likedFeedLoaderFactory.get(videoFeedType) {
                    PagedLoaderParams(
                        action = { from, count -> videoFeedApi.myLikedFeed(from = from, count = count) },
                        pageSize = 50,
                        nextPageIdFactory = { it.video.entityId },
                        mapper = {
                            it.likedFeedToVideoClipModelList(isExplicitlyLiked = true, likedVideos = emptyList())
                        },
                    )
                }
                else -> likedFeedLoaderFactory.get(videoFeedType) {
                    PagedLoaderParams(
                        action = { from, count ->
                            videoFeedApi
                                .likedFeed(userId = videoFeedType.userId, from = from, count = count)
                                .toLikedFeedBaseResponse()
                        },
                        pageSize = 50,
                        nextPageIdFactory = { it.video.entityId },
                        mapper = {
                            it.likedFeedToVideoClipModelList(
                                isExplicitlyLiked = false, likedVideos = getLikedVideos()
                            )
                        },
                    )
                }
            }
            else -> loaderFactory.get(videoFeedType) { type ->
                when (type) {
                    VideoFeedType.Main -> PagedLoaderParams(
                        action = { from, count -> videoFeedApi.feed(from, count) },
                        pageSize = 20,
                        nextPageIdFactory = { it.entityId },
                        mapper = { it.toVideoClipModelList(getLikedVideos()) },
                    )
                    VideoFeedType.Subscriptions -> PagedLoaderParams(
                        action = { from, count -> videoFeedApi.subscriptionFeed(from, count) },
                        pageSize = 5,
                        nextPageIdFactory = { it.entityId },
                        mapper = { it.toVideoClipModelList(getLikedVideos()) },
                    )
                    is VideoFeedType.Single -> PagedLoaderParams(
                        action = { _, _ -> videoFeedApi.feed(type.videoId, 1) },
                        pageSize = 1,
                        nextPageIdFactory = { null },
                        mapper = { it.toVideoClipModelList(getLikedVideos()) },
                    )
                    is VideoFeedType.Popular -> PagedLoaderParams(
                        action = { from, count -> videoFeedApi.popularFeed(from, count) },
                        pageSize = 50,
                        nextPageIdFactory = { it.entityId },
                        mapper = { it.toVideoClipModelList(getLikedVideos()) },
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
                        mapper = { it.toVideoClipModelList(getLikedVideos()) },
                    )
                    is VideoFeedType.Search -> PagedLoaderParams(
                        action = { from, count ->
                            videoFeedApi.searchFeed(
                                query = type.query,
                                from = from,
                                count = count
                            )
                        },
                        pageSize = 50,
                        nextPageIdFactory = { it.entityId },
                        mapper = { it.toVideoClipModelList(getLikedVideos()) },
                    )
                    is VideoFeedType.Liked -> throw IllegalStateException("Wrong handle place!")
                }
            }
        }
    }

    private suspend fun getLikedVideos(): List<UserLikedVideoResponseDto> {
        return likedVideos ?: apiCall(ioDispatcher) {
            // todo better way once back supports
            videoFeedApi.myLikedFeed(null, 1000)
        }.doOnSuccess {
            likedVideos = it
        }.dataOrCache ?: emptyList()
    }

    override fun getFeedState(feedType: VideoFeedType): StateFlow<VideoFeedPageModel> =
        getLoader(feedType).state

    override suspend fun refreshFeed(feedType: VideoFeedType): Effect<Completable> =
        getLoader(feedType).refresh()

    override suspend fun loadNextFeedPage(feedType: VideoFeedType): Effect<Completable> =
        getLoader(feedType).loadNext()

    override suspend fun markWatched(videoId: Uuid): Effect<Completable> {
        return apiCall(ioDispatcher) {
            videoFeedApi.view(videoId)
        }
    }

    override suspend fun like(videoId: Uuid): Effect<Completable> {
        return apiCall(ioDispatcher) {
            videoFeedApi.like(videoId)
        }
    }

    override suspend fun uploadVideo(title: String, fileId: Uuid, file: String): Effect<Uuid> {
        return apiCall(ioDispatcher) {
            videoFeedApi.addVideo(AddVideoRequestDto(title = title, description = title, thumbnailFileId = fileId))
        }.flatMap {
            videFeedUploader.upload(videoId = it.internalId, filePath = file)
        }
    }

    override suspend fun deleteVideo(videoId: Uuid): Effect<Completable> {
        return apiCall(ioDispatcher) {
            videoFeedApi.deleteVideo(videoId)
        }
    }

    override suspend fun markShowed(videoId: Uuid): Effect<Completable> {
        return apiCall(ioDispatcher) {
            videoFeedApi.showed(videoId)
        }
    }
}