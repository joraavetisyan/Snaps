package io.snaps.featurequests.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.snaps.basequests.domain.QuestInfoModel
import io.snaps.basequests.domain.QuestPageModel
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.date.toStringValue
import io.snaps.coreuicompose.uikit.listtile.EmptyListTileState
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.featurequests.presentation.ui.QuestTileState
import java.time.LocalDateTime

sealed interface HistoryQuestUiState {

    val id: Any

    data class Data(
        override val id: Any,
        val date: LocalDateTime,
        val item: QuestInfoModel,
        val onClicked: () -> Unit,
    ) : HistoryQuestUiState

    data class Shimmer(override val id: Any) : HistoryQuestUiState

    data class Progress(override val id: Any = -1000) : HistoryQuestUiState
}

data class HistoryQuestsUiState(
    val items: List<HistoryQuestUiState> = emptyList(),
    val errorState: MessageBannerState? = null,
    val emptyState: EmptyListTileState? = null,
    val onListEndReaching: (() -> Unit)? = null,
)

fun QuestPageModel.toHistoryQuestsUiState(
    shimmerListSize: Int,
    onItemClicked: (QuestInfoModel) -> Unit,
    onReloadClicked: () -> Unit,
    onListEndReaching: () -> Unit,
): HistoryQuestsUiState {
    return when {
        isLoading && loadedPageItems.isEmpty() -> HistoryQuestsUiState(
            items = List(shimmerListSize) {
                HistoryQuestUiState.Shimmer("${HistoryQuestUiState.Shimmer::class.simpleName}$it")
            }
        )
        error != null -> HistoryQuestsUiState(
            errorState = MessageBannerState.defaultState(onReloadClicked)
        )
        loadedPageItems.isEmpty() -> HistoryQuestsUiState(
            emptyState = EmptyListTileState.defaultState(),
        )
        else -> HistoryQuestsUiState(
            items = loadedPageItems.flatMap { questModel ->
                questModel.quests.map {
                    HistoryQuestUiState.Data(
                        id = questModel.id,
                        date = questModel.questDate,
                        item = it,
                        onClicked = { onItemClicked(it) },
                    )
                }
            }.run {
                if (nextPageId == null) this
                else this.plus(HistoryQuestUiState.Progress())
            },
            onListEndReaching = onListEndReaching,
        )
    }
}

fun LazyListScope.historyQuestsItems(
    uiState: HistoryQuestsUiState,
    modifier: Modifier = Modifier,
) {
    items(uiState.items) {
        when (it) {
            is HistoryQuestUiState.Data -> QuestTileState.Data(
                title = it.item.type.toQuestTitle(it.item.count),
                description = it.date.toStringValue().textValue(),
                energy = it.item.energy,
                energyProgress = it.item.energyProgress(),
                status = null,
                clickListener = it.onClicked,
            ).Content(modifier = modifier)
            is HistoryQuestUiState.Shimmer -> QuestTileState.Shimmer.Content(modifier = modifier)
            is HistoryQuestUiState.Progress -> Box(modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center),
                )
            }
        }
    }
    uiState.emptyState?.let {
        item {
            it.Content(modifier = modifier)
        }
    }
    uiState.errorState?.let {
        item {
            it.Content(modifier = modifier)
        }
    }
}