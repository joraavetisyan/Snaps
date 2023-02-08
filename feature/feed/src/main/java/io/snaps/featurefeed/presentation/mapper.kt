package io.snaps.featurefeed.presentation

import io.snaps.baseplayer.domain.VideoClipModel
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.featurefeed.domain.VideoFeedPageModel

data class VideoFeedUiState(
    val clips: List<VideoClipModel> = emptyList(),
    val errorState: MessageBannerState? = null,
    val onListEndReaching: (() -> Unit)? = null,
)

fun VideoFeedPageModel.toVideoFeedUiState(
    onClipClicked: (VideoClipModel) -> Unit,
    onReloadClicked: () -> Unit,
    onListEndReaching: () -> Unit,
): VideoFeedUiState {
    return when {
        isLoading && loadedPageItems.isEmpty() -> VideoFeedUiState(clips = emptyList())
        error != null -> VideoFeedUiState(
            errorState = MessageBannerState.defaultState(onReloadClicked)
        )
        loadedPageItems.isEmpty() -> VideoFeedUiState(clips = emptyList())
        else -> VideoFeedUiState(
            clips = loadedPageItems,
            onListEndReaching = onListEndReaching,
        )
    }
}