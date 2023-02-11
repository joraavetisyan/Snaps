package io.snaps.basefeed.ui

import io.snaps.basefeed.domain.VideoFeedPageModel
import io.snaps.baseplayer.domain.VideoClipModel
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.coreuicompose.uikit.listtile.EmptyListTileState
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState

sealed interface VideoClipUiState {

    val id: Any

    data class Data(
        override val id: Any,
        val clip: VideoClipModel,
        val onClicked: () -> Unit,
    ) : VideoClipUiState

    data class Shimmer(override val id: Any) : VideoClipUiState
}

data class VideoFeedUiState(
    val items: List<VideoClipUiState> = emptyList(),
    val errorState: MessageBannerState? = null,
    val emptyState: EmptyListTileState? = null,
    val onListEndReaching: (() -> Unit)? = null,
)

fun VideoFeedPageModel.toVideoFeedUiState(
    shimmerListSize: Int,
    onClipClicked: (VideoClipModel) -> Unit,
    onReloadClicked: () -> Unit,
    onListEndReaching: () -> Unit,
): VideoFeedUiState {
    return when {
        isLoading && loadedPageItems.isEmpty() -> VideoFeedUiState(
            items = List(shimmerListSize) {
                VideoClipUiState.Shimmer("${VideoClipUiState.Shimmer::class.simpleName}$it")
            }
        )
        error != null -> VideoFeedUiState(
            errorState = MessageBannerState.defaultState(onReloadClicked)
        )
        loadedPageItems.isEmpty() -> VideoFeedUiState(
            emptyState = EmptyListTileState(
                message = "No data".textValue(),
                image = ImageValue.ResImage(R.drawable.img_diamonds),
            )
        )
        else -> VideoFeedUiState(
            items = loadedPageItems.map {
                VideoClipUiState.Data(
                    id = it.id,
                    clip = it,
                    onClicked = { onClipClicked(it) },
                )
            },
            onListEndReaching = onListEndReaching,
        )
    }
}