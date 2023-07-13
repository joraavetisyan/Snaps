package io.snaps.basefeed.domain

import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.data.toVideoModel
import io.snaps.basesettings.data.SettingsRepository
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import javax.inject.Inject

interface VideoFeedInteractor {

    suspend fun insertAds(pageModel: VideoFeedPageModel): VideoFeedPageModel
}

private const val SHOW_AD_PLACE = 10

class VideoFeedInteractorImpl @Inject constructor(
    private val action: Action,
    private val settingsRepository: SettingsRepository,
    @Bridged private val videoFeedRepository: VideoFeedRepository,
) : VideoFeedInteractor {

    override suspend fun insertAds(pageModel: VideoFeedPageModel): VideoFeedPageModel {
        val ads = settingsRepository.state.value.dataOrCache?.ads ?: return pageModel
        val result = pageModel.loadedPageItems.toMutableList()
        ads.forEachIndexed { index, ad ->
            if (ad.isShown) {
                val likeCount = action.execute(needsErrorProcessing = false) {
                    videoFeedRepository.get(videoId = ad.entityId)
                }.map {
                    it.likeCount
                }.data ?: 0
                val adVideo = ad.toVideoModel().copy(likeCount = likeCount)

                val startIndex = SHOW_AD_PLACE * (index + 1)
                val endIndex = pageModel.loadedPageItems.lastIndex
                val step = ads.size * SHOW_AD_PLACE
                for (i in startIndex..endIndex step step) {
                    result.add(i, adVideo)
                }
            }
        }
        return pageModel.copy(loadedPageItems = result)
    }
}