package io.snaps.featuretasks.presentation

import io.snaps.corecommon.model.Effect
import io.snaps.featuretasks.domain.TaskModel
import io.snaps.featuretasks.presentation.ui.TaskTileState

fun Effect<List<TaskModel>>.toTaskTileState(
    onItemClicked: (TaskModel) -> Unit,
    onReloadClicked: () -> Unit,
) = when {
    isSuccess -> {
        requireData.map {
            it.toTaskTileState(onItemClicked = onItemClicked)
        }
    }
    else -> listOf(TaskTileState.Error(clickListener = onReloadClicked))
}

private fun TaskModel.toTaskTileState(
    onItemClicked: (TaskModel) -> Unit,
) = TaskTileState.Data(
    id = id,
    title = title,
    description = description,
    energy = energy,
    energyProgress = energyProgress,
    done = done,
    clickListener = { onItemClicked(this) },
)