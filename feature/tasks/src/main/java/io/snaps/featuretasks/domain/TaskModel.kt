package io.snaps.featuretasks.domain

import io.snaps.corecommon.model.Uuid
import io.snaps.featuretasks.data.model.TaskType

data class TaskModel(
    val id: Uuid,
    val title: String,
    val description: String,
    val energy: Int,
    val energyProgress: Int,
    val type: TaskType,
    val count: Int,
    val madeCount: Int,
    val done: Boolean,
)