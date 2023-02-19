package io.snaps.featuretasks.data

import io.snaps.featuretasks.data.model.TaskItemResponseDto
import io.snaps.featuretasks.domain.TaskModel

fun List<TaskItemResponseDto>.toModelList() = map(TaskItemResponseDto::toTaskModel)

fun TaskItemResponseDto.toTaskModel() = TaskModel(
    id = id,
    title = title,
    description = description,
    type = type,
    energy = energy,
    energyProgress = energyProgress,
    count = count ?: 0,
    madeCount = count ?: 0,
    done = done,
)