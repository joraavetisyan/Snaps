package io.snaps.baseprofile.domain

import io.snaps.baseprofile.data.model.QuestType
import io.snaps.corecommon.model.SocialNetwork

data class QuestModel(
    val energy: Int,
    val energyProgress: Int,
    val type: QuestType,
    val count: Int,
    val madeCount: Int,
    val completed: Boolean,
    val network: SocialNetwork?,
)