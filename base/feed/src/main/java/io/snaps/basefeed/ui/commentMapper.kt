package io.snaps.basefeed.ui

import io.snaps.basefeed.domain.CommentModel
import io.snaps.basefeed.domain.CommentPageModel
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.coreuicompose.uikit.listtile.EmptyListTileState
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState

sealed interface CommentUiState {

    val id: Any

    data class Data(
        override val id: Any,
        val item: CommentModel,
        val onClicked: () -> Unit = {},
    ) : CommentUiState

    data class Shimmer(override val id: Any) : CommentUiState

    data class Progress(override val id: Any = -1000) : CommentUiState
}

data class CommentsUiState(
    val items: List<CommentUiState> = emptyList(),
    val errorState: MessageBannerState? = null,
    val emptyState: EmptyListTileState? = null,
    val onListEndReaching: (() -> Unit)? = null,
)

fun CommentPageModel.toCommentsUiState(
    shimmerListSize: Int,
    onCommentClicked: (CommentModel) -> Unit,
    onReloadClicked: () -> Unit,
    onListEndReaching: () -> Unit,
): CommentsUiState {
    return when {
        isLoading && loadedPageItems.isEmpty() -> CommentsUiState(
            items = List(shimmerListSize) {
                CommentUiState.Shimmer("${CommentUiState.Shimmer::class.simpleName}$it")
            }
        )
        error != null -> CommentsUiState(
            errorState = MessageBannerState.defaultState(onReloadClicked)
        )
        loadedPageItems.isEmpty() -> CommentsUiState(
            emptyState = EmptyListTileState(
                title = "No data".textValue(),
                image = ImageValue.ResImage(R.drawable.img_diamonds),
            )
        )
        else -> CommentsUiState(
            items = loadedPageItems.map {
                CommentUiState.Data(
                    id = it.id,
                    item = it,
                    onClicked = { onCommentClicked(it) },
                )
            }.run {
                if (nextPage == null) this
                else this.plus(CommentUiState.Progress())
            },
            onListEndReaching = onListEndReaching,
        )
    }
}