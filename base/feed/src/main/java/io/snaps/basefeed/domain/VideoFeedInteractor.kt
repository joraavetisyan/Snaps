package io.snaps.basefeed.domain

import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.data.toVideoModel
import io.snaps.basesettings.data.SettingsRepository
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import kotlinx.coroutines.flow.update
import javax.inject.Inject

interface VideoFeedInteractor {

    suspend fun insertAds(pageModel: VideoFeedPageModel): VideoFeedPageModel
}

class VideoFeedInteractorImpl @Inject constructor(
    private val action: Action,
    private val settingsRepository: SettingsRepository,
    @Bridged private val videoFeedRepository: VideoFeedRepository,
) : VideoFeedInteractor {

    override suspend fun insertAds(pageModel: VideoFeedPageModel): VideoFeedPageModel {
        val ad = settingsRepository.state.value.dataOrCache?.ad
        return if (ad != null && ad.isShown) {
            val chunked: List<List<VideoClipModel>> = pageModel.loadedPageItems.chunked(ad.showPlace)
            val result = mutableListOf<VideoClipModel>()
            val likeCount = action.execute(needsErrorProcessing = false) {
                videoFeedRepository.get(ad.entityId)
            }.map {
                it.likeCount
            }.data ?: 0
            val adVideo = ad.toVideoModel().copy(
                likeCount = likeCount
            )
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