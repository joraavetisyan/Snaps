package io.snaps.featureprofile.presentation.screen

import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.coreuicompose.uikit.listtile.EmptyListTileState
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.featureprofile.domain.SubModel
import io.snaps.featureprofile.domain.SubPageModel

sealed interface SubUiState {

    val userId: Any

    data class Data(
        override val userId: Any,
        val item: SubModel,
        val onClicked: () -> Unit,
        val onSubscribeClicked: () -> Unit,
    ) : SubUiState

    data class Shimmer(override val userId: Any) : SubUiState

    data class Progress(override val userId: Any = -1000) : SubUiState
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
            emptyState = EmptyListTileState(
                message = "No data".textValue(),
                image = ImageValue.ResImage(R.drawable.img_diamonds),
            )
        )
        else -> SubsUiState(
            items = loadedPageItems.map {
                SubUiState.Data(
                    userId = it.userId,
                    item = it,
                    onClicked = { onItemClicked(it) },
                    onSubscribeClicked = { onSubscribeClicked(it) }
                )
            }.run {
                if (nextPage == null) this
                else this.plus(SubUiState.Progress())
            },
            onListEndReaching = onListEndReaching,
        )
    }
}