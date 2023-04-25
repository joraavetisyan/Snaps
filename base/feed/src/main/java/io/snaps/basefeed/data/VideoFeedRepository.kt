package io.snaps.basefeed.data

import android.net.Uri
import androidx.core.net.toFile
import io.snaps.basefeed.data.model.AddVideoRequestDto
import io.snaps.basefeed.data.model.UserLikedVideoFeedItemResponseDto
import io.snaps.basefeed.domain.VideoFeedPageModel
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.baseplayer.domain.VideoClipModel
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.model.generateCurrentDateTime
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.PagedLoader
import io.snaps.coredata.network.PagedLoaderParams
import io.snaps.coredata.network.apiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject

interface VideoFeedRepository {

    fun getFeedState(feedType: VideoFeedType): StateFlow<VideoFeedPageModel>

    suspend fun refreshFeed(feedType: VideoFeedType): Effect<Completable>

    suspend fun loadNextFeedPage(feedType: VideoFeedType): Effect<Completable>

    suspend fun view(videoId: Uuid): Effect<Completable>

    suspend fun like(videoId: Uuid): Effect<Completable>

    suspend fun addVideo(title: String, description: String, fileId: Uuid): Effect<VideoClipModel>

    suspend fun uploadVideo(uri: Uri, videoId: Uuid): Effect<Completable>

    suspend fun deleteVideo(videoId: Uuid): Effect<Completable>
}

class VideoFeedRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val videoFeedApi: VideoFeedApi,
    private val loaderFactory: VideoFeedLoaderFactory,
    private val userLikedVideoFeedLoaderFactory: UserLikedVideoFeedLoaderFactory,
) : VideoFeedRepository {

    private var likedVideos: List<UserLikedVideoFeedItemResponseDto>? = null

    private fun getLoader(videoFeedType: VideoFeedType): PagedLoader<*, VideoClipModel> {
        return when (videoFeedType) {
            is VideoFeedType.UserLiked -> userLikedVideoFeedLoaderFactory.get(Unit) {
                PagedLoaderParams(
                    action = { from, count -> videoFeedApi.likedVideos(from, count) },
                    pageSize = 20,
                    nextPageIdFactory = { it.entityId },
                    mapper = { videoFeed -> videoFeed.map { it.video.toModel(true) } },
                )
            }
            else -> loaderFactory.get(videoFeedType) { type ->
                when (type) {
                    VideoFeedType.Main -> PagedLoaderParams(
                        action = { from, count -> videoFeedApi.feed(from, count) },
                        pageSize = 3,
                        nextPageIdFactory = { it.entityId },
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

    private suspend fun getLikedVideos(): List<UserLikedVideoFeedItemResponseDto> {
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

    override suspend fun addVideo(
        title: String,
        description: String,
        fileId: Uuid,
    ): Effect<VideoClipModel> {
        return apiCall(ioDispatcher) {
            videoFeedApi.addVideo(
                AddVideoRequestDto(
                    title = title,
                    description = description,
                    thumbnailFileId = fileId,
                )
            )
        }.map {
            it.toModel(isLiked = false)
        }
    }

    override suspend fun uploadVideo(uri: Uri, videoId: Uuid): Effect<Completable> {
        val file = uri.buildUpon().scheme("file").build().toFile()
        val mediaType = "multipart/form-data".toMediaType()

        val multipartBody = MultipartBody.Part.createFormData(
            name = "videoFile",
            filename = "file_${generateCurrentDateTime()}.mp4",
            body = file.asRequestBody(mediaType),
        )

        return apiCall(ioDispatcher) {
            videoFeedApi.uploadVideo(file = multipartBody, videoId = videoId)
        }
    }

    override suspend fun deleteVideo(videoId: Uuid): Effect<Completable> {
        return apiCall(ioDispatcher) {
            videoFeedApi.deleteVideo(videoId)
        }
    }
}