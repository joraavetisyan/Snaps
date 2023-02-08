package io.snaps.featurefeed.data

import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.featurefeed.domain.VideoFeedPageModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface VideoFeedRepository {

    fun getState(): StateFlow<VideoFeedPageModel>

    suspend fun updateData(): Effect<Completable>

    suspend fun loadNextPage(): Effect<Completable>
}

class VideoFeedRepositoryImpl @Inject constructor(
    private val videoFeedLoader: VideoFeedLoader,
) : VideoFeedRepository {

    override fun getState(): StateFlow<VideoFeedPageModel> = videoFeedLoader.state

    override suspend fun updateData(): Effect<Completable> = videoFeedLoader.update()

    override suspend fun loadNextPage(): Effect<Completable> = videoFeedLoader.loadNextPage()
}