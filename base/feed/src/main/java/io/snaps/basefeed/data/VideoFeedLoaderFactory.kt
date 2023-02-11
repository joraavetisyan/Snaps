package io.snaps.basefeed.data

import dagger.assisted.AssistedFactory
import javax.inject.Singleton

@Singleton
@AssistedFactory
abstract class VideoFeedLoaderFactory {

    private val loadersMap: HashMap<VideoFeedType, VideoFeedLoader> = hashMapOf()

    fun get(
        videoFeedType: VideoFeedType,
        params: (VideoFeedType) -> VideoFeedLoaderParams,
    ): VideoFeedLoader {
        return loadersMap.getOrPut(videoFeedType) { create(params(videoFeedType)) }
    }

    abstract fun create(params: VideoFeedLoaderParams): VideoFeedLoader
}