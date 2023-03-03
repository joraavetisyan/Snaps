package io.snaps.basefeed.data

import io.snaps.basefeed.data.model.AddVideoRequestDto
import io.snaps.basefeed.data.model.ShareInfoRequestDto
import io.snaps.basefeed.domain.VideoFeedPageModel
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.baseplayer.domain.VideoClipModel
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.SocialNetwork
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.PagedLoaderParams
import io.snaps.coredata.network.apiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface VideoFeedRepository {

    fun getFeedState(feedType: VideoFeedType): StateFlow<VideoFeedPageModel>

    suspend fun refreshFeed(feedType: VideoFeedType): Effect<Completable>

    suspend fun loadNextFeedPage(feedType: VideoFeedType): Effect<Completable>

    suspend fun like(videoId: Uuid): Effect<Completable>

    suspend fun addVideo(title: String, description: String, fileId: Uuid): Effect<VideoClipModel>

    suspend fun shareInfo(socialNetwork: SocialNetwork): Effect<Completable>
}

class VideoFeedRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val videoFeedApi: VideoFeedApi,
    private val loaderFactory: VideoFeedLoaderFactory,
) : VideoFeedRepository {

    private fun getLoader(currencyType: VideoFeedType): VideoFeedLoader {
        return loaderFactory.get(currencyType) {
            when (it) {
                VideoFeedType.Main -> PagedLoaderParams(
                    action = videoFeedApi::feed,
                    pageSize = 3,
                )
                is VideoFeedType.Popular -> PagedLoaderParams(
                    action = videoFeedApi::popularFeed,
                    pageSize = 12,
                )
                is VideoFeedType.User -> PagedLoaderParams(
                    action = videoFeedApi::feed,
                    pageSize = 15,
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
            it.toModel()
        }
    }

    override suspend fun shareInfo(socialNetwork: SocialNetwork): Effect<Completable> {
        return apiCall(ioDispatcher) {
            videoFeedApi.shareInfo(
                body = ShareInfoRequestDto(socialNetwork)
            )
        }
    }
}