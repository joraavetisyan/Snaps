package io.snaps.featuretasks.presentation

import io.snaps.baseprofile.domain.QuestModel
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.featuretasks.presentation.ui.TaskTileState

fun State<List<QuestModel>>.toTaskTileState(
    onItemClicked: (QuestModel) -> Unit,
    onReloadClicked: () -> Unit,
) = when (this) {
    is Loading -> List(6) { TaskTileState.Shimmer }
    is Effect -> when {
        isSuccess -> requireData.map {
            it.toTaskTileState(onItemClicked = onItemClicked)
        }
        else -> listOf(TaskTileState.Error(clickListener = onReloadClicked))
    }
}

private fun QuestModel.toTaskTileState(
    onItemClicked: (QuestModel) -> Unit,
) = TaskTileState.Data(
    energy = energy,
    energyProgress = energyProgress,
    done = completed,
    type = type,
    clickListener = { onItemClicked(this) },
)