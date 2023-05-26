package io.snaps.basefeed.ui

import io.snaps.basefeed.domain.VideoFeedPageModel
import io.snaps.basefeed.domain.VideoClipModel
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.uikit.listtile.EmptyListTileState
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState

sealed interface VideoClipUiState {

    val key: Any

    data class Data(
        override val key: Any,
        val clip: VideoClipModel,
        val onClicked: () -> Unit,
    ) : VideoClipUiState

    data class Shimmer(override val key: Any) : VideoClipUiState
}

data class VideoFeedUiState(
    val items: List<VideoClipUiState> = emptyList(),
    val errorState: MessageBannerState? = null,
    val emptyState: EmptyListTileState? = null,
    val onListEndReaching: (() -> Unit)? = null,
) {

    val isData get() = items.any { it is VideoClipUiState.Data }

    val dataSize get() = items.filterIsInstance<VideoClipUiState.Data>().size
}

fun VideoFeedPageModel.toVideoFeedUiState(
    shimmerListSize: Int,
    emptyMessage: TextValue = StringKey.MessageEmptyVideoFeed.textValue(),
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
            emptyState = EmptyListTileState.defaultState(title = emptyMessage)
        )
        else -> VideoFeedUiState(
            items = loadedPageItems.map {
                VideoClipUiState.Data(
                    key = it.id,
                    clip = it,
                    onClicked = { onClipClicked(it) },
                )
            },
            onListEndReaching = onListEndReaching,
        )
    }
}