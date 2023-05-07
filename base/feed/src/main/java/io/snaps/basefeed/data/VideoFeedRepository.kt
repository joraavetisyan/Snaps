package io.snaps.basefeed.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.snaps.basefeed.data.model.AddVideoRequestDto
import io.snaps.basefeed.data.model.UserLikedVideoResponseDto
import io.snaps.basefeed.domain.VideoFeedPageModel
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.baseplayer.domain.VideoClipModel
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.BuildInfo
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.database.TokenStorage
import io.snaps.coredata.network.ApiService
import io.snaps.coredata.network.PagedLoader
import io.snaps.coredata.network.PagedLoaderParams
import io.snaps.coredata.network.apiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest
import javax.inject.Inject

interface VideoFeedRepository {

    fun getFeedState(feedType: VideoFeedType): StateFlow<VideoFeedPageModel>

    suspend fun refreshFeed(feedType: VideoFeedType): Effect<Completable>

    suspend fun loadNextFeedPage(feedType: VideoFeedType): Effect<Completable>

    suspend fun view(videoId: Uuid): Effect<Completable>

    suspend fun like(videoId: Uuid): Effect<Completable>

    suspend fun uploadVideo(
        title: String,
        description: String,
        fileId: Uuid,
        filePath: String,
    ): Effect<Uuid>

    suspend fun deleteVideo(videoId: Uuid): Effect<Completable>
}

class VideoFeedRepositoryImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val buildInfo: BuildInfo,
    private val tokenStorage: TokenStorage,
    private val videoFeedApi: VideoFeedApi,
    private val loaderFactory: VideoFeedLoaderFactory,
    private val userLikedVideoFeedLoaderFactory: UserLikedVideoFeedLoaderFactory,
) : VideoFeedRepository {

    private var likedVideos: List<UserLikedVideoResponseDto>? = null

    private fun getLoader(videoFeedType: VideoFeedType): PagedLoader<*, VideoClipModel> {
        return when (videoFeedType) {
            is VideoFeedType.UserLiked -> userLikedVideoFeedLoaderFactory.get(Unit) {
                PagedLoaderParams(
                    action = { from, count -> videoFeedApi.likedVideos(from, count) },
                    pageSize = 20,
                    nextPageIdFactory = { it.entityId },
                    mapper = { videoFeed -> videoFeed.map { it.video.toModel() } },
                )
            }
            else -> loaderFactory.get(videoFeedType) { type ->
                when (type) {
                    VideoFeedType.Main -> PagedLoaderParams(
                        action = { from, count -> videoFeedApi.feed(from, count) },
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
                        pageSize = 12,
                        nextPageIdFactory = { it.entityId },
                        mapper = { it.toVideoClipModelList(getLikedVideos()) },
                    )
                    is VideoFeedType.User -> PagedLoaderParams(
                        action = { from, count ->
                            if (type.userId != null) {
                                videoFeedApi.userFeed(type.userId, from, count)
                            } else {
                                videoFeedApi.myFeed(from, count)
                            }
                        },
                        pageSize = 20,
                        nextPageIdFactory = { it.entityId },
                        mapper = { it.toVideoClipModelList(getLikedVideos()) },
                    )
                    is VideoFeedType.All -> PagedLoaderParams(
                        action = { from, count -> videoFeedApi.videos(type.query, from, count) },
                        pageSize = 12,
                        nextPageIdFactory = { it.entityId },
                        mapper = { it.toVideoClipModelList(getLikedVideos()) },
                    )
                    is VideoFeedType.UserLiked -> throw IllegalStateException("Unknown video type")
                }
            }
        }
    }

    private suspend fun getLikedVideos(): List<UserLikedVideoResponseDto> {
        return likedVideos ?: apiCall(ioDispatcher) {
            videoFeedApi.likedVideos(null, 100)
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

    override suspend fun view(videoId: Uuid): Effect<Completable> {
        return apiCall(ioDispatcher) {
            videoFeedApi.view(videoId)
        }
    }

    override suspend fun like(videoId: Uuid): Effect<Completable> {
        return apiCall(ioDispatcher) {
            videoFeedApi.like(videoId)
        }
    }

    override suspend fun uploadVideo(
        title: String,
        description: String,
        fileId: Uuid,
        filePath: String,
    ): Effect<Uuid> {
        return apiCall(ioDispatcher) {
            videoFeedApi.addVideo(
                AddVideoRequestDto(
                    title = title,
                    description = description,
                    thumbnailFileId = fileId,
                )
            )
        }.flatMap {
            uploadVideo(filePath, it.entityId)
        }
    }

    private fun uploadVideo(filePath: String, videoId: Uuid): Effect<Uuid> {
        try {
            val uploadId = MultipartUploadRequest(
                context = applicationContext,
                serverUrl = ApiService.General.getBaseUrl(buildInfo) + "$videoId/upload",
            ).apply {
                setMethod("POST")
                addHeader("Authorization", "${tokenStorage.authToken}")
                addFileToUpload(
                    filePath = filePath,
                    parameterName = "videoFile",
                )
            }.startUpload()
            return Effect.success(uploadId)
        } catch (e: Exception) {
            log(e)
            return Effect.error(AppError.Unknown(cause = e))
        }
    }

    override suspend fun deleteVideo(videoId: Uuid): Effect<Completable> {
        return apiCall(ioDispatcher) {
            videoFeedApi.deleteVideo(videoId)
        }
    }
}