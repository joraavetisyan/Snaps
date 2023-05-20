package io.snaps.featureprofile.presentation.screen

import io.snaps.coreuicompose.uikit.listtile.EmptyListTileState
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.basesubs.domain.SubModel
import io.snaps.basesubs.domain.SubPageModel

sealed interface SubUiState {

    val key: Any

    data class Data(
        override val key: Any,
        val item: SubModel,
        val onClicked: () -> Unit,
        val onSubscribeClicked: () -> Unit,
    ) : SubUiState

    data class Shimmer(override val key: Any) : SubUiState

    data class Progress(override val key: Any = -1000) : SubUiState
}

data class SubsUiState(
    val items: List<SubUiState> = emptyList(),
    val errorState: MessageBannerState? = null,
    val emptyState: EmptyListTileState? = null,
    val onListEndReaching: (() -> Unit)? = null,
)

fun SubPageModel.toSubsUiState(
    shimmerListSize: Int,
    onItemClicked: (SubModel) -> Unit,
    onSubscribeClicked: (SubModel) -> Unit,
    onReloadClicked: () -> Unit,
    onListEndReaching: () -> Unit,
): SubsUiState {
    return when {
        isLoading && loadedPageItems.isEmpty() -> SubsUiState(
            items = List(shimmerListSize) {
                SubUiState.Shimmer("${SubUiState.Shimmer::class.simpleName}$it")
            }
        )
        error != null -> SubsUiState(
            errorState = MessageBannerState.defaultState(onReloadClicked)
        )
        loadedPageItems.isEmpty() -> SubsUiState(
            emptyState = EmptyListTileState.defaultState(),
        )
        else -> if (loadedPageItems.any { it.isSubscribed == null }) {
            SubsUiState(errorState = MessageBannerState.defaultState(onReloadClicked))
        } else {
            SubsUiState(
                items = loadedPageItems.map {
                    SubUiState.Data(
                        key = it.entityId,
                        item = it,
                        onClicked = { onItemClicked(it) },
                        onSubscribeClicked = { onSubscribeClicked(it) }
                    )
                }.run {
                    if (nextPageId == null) this
                    else this.plus(SubUiState.Progress())
                },
                onListEndReaching = onListEndReaching,
            )
        }
    }
}