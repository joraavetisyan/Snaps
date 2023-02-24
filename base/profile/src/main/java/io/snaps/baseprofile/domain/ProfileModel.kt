package io.snaps.baseprofile.domain

import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.model.Uuid
import java.time.LocalDateTime

// todo rename UserModel
data class ProfileModel(
    val entityId: Uuid,
    val createdDate: LocalDateTime,
    val userId: Uuid,
    val email: String,
    val wallet: String,
    val name: String,
    val totalLikes: Int,
    val totalSubscribers: Int,
    val totalSubscriptions: Int,
    val totalPublication: Int,
    val avatar: ImageValue,
    val hasNft: Boolean,
    val experience: Int,
    val level: Int,
    val quests: List<QuestModel>
)