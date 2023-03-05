package io.snaps.featuretasks.domain

import io.snaps.corecommon.mock.rBool
import io.snaps.corecommon.model.Uuid
import java.time.LocalDateTime

data class TaskModel(
    val id: Uuid,
    val userId: Uuid,
    val date: LocalDateTime,
    val experience: Int,
    val energy: Int,
    val energyProgress: Int = 5, // todo
    val completed: Boolean = rBool, // todo
)