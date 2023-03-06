package io.snaps.baseprofile.domain

import io.snaps.corecommon.model.QuestType
import io.snaps.corecommon.model.SocialNetwork
import java.time.LocalDateTime

data class QuestInfoModel(
    val quests: List<QuestModel>,
    val totalEnergy: Int,
    val totalEnergyProgress: Int,
    val questDate: LocalDateTime,
)

data class QuestModel(
    val energy: Int,
    val energyProgress: Int,
    val type: QuestType,
    val completed: Boolean,
    val network: SocialNetwork?,
)