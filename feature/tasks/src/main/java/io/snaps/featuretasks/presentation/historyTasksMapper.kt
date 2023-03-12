package io.snaps.featuretasks.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.date.toStringValue
import io.snaps.coreuicompose.uikit.listtile.EmptyListTileState
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.featuretasks.domain.TaskModel
import io.snaps.featuretasks.domain.TaskPageModel
import io.snaps.featuretasks.presentation.ui.TaskTileState

sealed interface HistoryTaskUiState {

    val id: Any

    data class Data(
        override val id: Any,
        val item: TaskModel,
        val onClicked: () -> Unit,
    ) : HistoryTaskUiState

    data class Shimmer(override val id: Any) : HistoryTaskUiState

    data class Progress(override val id: Any = -1000) : HistoryTaskUiState
}

data class HistoryTasksUiState(
    val items: List<HistoryTaskUiState> = emptyList(),
    val errorState: MessageBannerState? = null,
    val emptyState: EmptyListTileState? = null,
    val onListEndReaching: (() -> Unit)? = null,
)

fun TaskPageModel.toHistoryTasksUiState(
    shimmerListSize: Int,
    onItemClicked: (TaskModel) -> Unit,
    onReloadClicked: () -> Unit,
    onListEndReaching: () -> Unit,
): HistoryTasksUiState {
    return when {
        isLoading && loadedPageItems.isEmpty() -> HistoryTasksUiState(
            items = List(shimmerListSize) {
                HistoryTaskUiState.Shimmer("${HistoryTaskUiState.Shimmer::class.simpleName}$it")
            }
        )
        error != null -> HistoryTasksUiState(
            errorState = MessageBannerState.defaultState(onReloadClicked)
        )
        loadedPageItems.isEmpty() -> HistoryTasksUiState(
            emptyState = EmptyListTileState(
                title = "No data".textValue(),
                image = ImageValue.ResImage(R.drawable.img_diamonds),
            )
        )
        else -> HistoryTasksUiState(
            items = loadedPageItems.map {
                HistoryTaskUiState.Data(
                    id = it.id,
                    item = it,
                    onClicked = { onItemClicked(it) },
                )
            }.run {
                if (nextPage == null) this
                else this.plus(HistoryTaskUiState.Progress())
            },
            onListEndReaching = onListEndReaching,
        )
    }
}

fun LazyListScope.historyTasksItems(
    uiState: HistoryTasksUiState,
    modifier: Modifier = Modifier,
) {
    items(uiState.items, key = { it.id }) {
        when (it) {
            is HistoryTaskUiState.Data -> TaskTileState.Data(
                title = "Name tasks", // todo
                description = it.item.date.toStringValue(),
                energy = it.item.energy,
                energyProgress = it.item.energyProgress,
                done = it.item.completed,
                clickListener = it.onClicked,
            ).Content(modifier = modifier)
            is HistoryTaskUiState.Shimmer -> TaskTileState.Shimmer.Content(modifier = modifier)
            is HistoryTaskUiState.Progress -> Box(modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center),
                )
            }
        }
    }
}