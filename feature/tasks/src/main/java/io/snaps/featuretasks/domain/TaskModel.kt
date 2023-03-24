package io.snaps.featuretasks.domain

import io.snaps.baseprofile.domain.QuestModel
import io.snaps.corecommon.model.Uuid
import java.time.LocalDateTime

data class TaskModel(
    val id: Uuid,
    val userId: Uuid,
    val date: LocalDateTime,
    val experience: Int,
    val quests: List<QuestModel>?,
)