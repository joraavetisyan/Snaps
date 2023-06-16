package io.snaps.basefeed.domain

import io.snaps.basefeed.data.toVideoModel
import io.snaps.basesettings.data.SettingsRepository
import javax.inject.Inject

interface VideoFeedInteractor {

    suspend fun insertAds(pageModel: VideoFeedPageModel): VideoFeedPageModel
}

class VideoFeedInteractorImpl @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : VideoFeedInteractor {

    override suspend fun insertAds(pageModel: VideoFeedPageModel): VideoFeedPageModel {
        val ad = settingsRepository.state.value.dataOrCache?.ad
        return if (ad != null && ad.isShown) {
            val chunked: List<List<VideoClipModel>> = pageModel.loadedPageItems.chunked(ad.showPlace)
            val result = mutableListOf<VideoClipModel>()
            val adVideo = ad.toVideoModel()
            chunked.forEach {
                result.addAll(it)
                if (it.size == ad.showPlace) {
                    result.add(adVideo)
                }
            }
            pageModel.copy(loadedPageItems = result)
        } else {
            pageModel
        }
    }
}