package io.snaps.basequests.domain

import io.snaps.basequests.data.model.SocialPostStatus
import io.snaps.corecommon.model.QuestType
import io.snaps.corecommon.model.Uuid
import java.time.LocalDateTime

data class QuestModel(
    val id: Uuid,
    val userId: Uuid,
    val roundId: Uuid,
    val experience: Int,
    val quests: List<QuestInfoModel>,
    val totalEnergy: Int,
    val totalEnergyProgress: Int,
    val questDate: LocalDateTime,
)

data class QuestInfoModel(
    val energy: Int,
    val type: QuestType,
    val completed: Boolean,
    val madeCount: Int?,
    val count: Int?,
    val status: SocialPostStatus?,
)