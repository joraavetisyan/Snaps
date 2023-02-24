package io.snaps.baseprofile.domain

import io.snaps.baseprofile.data.model.QuestType

data class QuestModel(
    val energy: Int,
    val energyProgress: Int,
    val type: QuestType,
    val count: Int,
    val madeCount: Int,
    val completed: Boolean,
    val network: String?,
)