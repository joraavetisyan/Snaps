package io.snaps.baseprofile.domain

import io.snaps.baseprofile.data.model.SocialPostStatus
import io.snaps.corecommon.model.TaskType
import java.time.LocalDateTime

data class QuestInfoModel(
    val quests: List<QuestModel>,
    val totalEnergy: Int,
    val totalEnergyProgress: Int,
    val questDate: LocalDateTime,
)

data class QuestModel(
    val energy: Int,
    val type: TaskType,
    val completed: Boolean,
    val madeCount: Int?,
    val count: Int?,
    val status: SocialPostStatus?,
)