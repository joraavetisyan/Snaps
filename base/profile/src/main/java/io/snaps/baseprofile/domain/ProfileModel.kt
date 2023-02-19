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
    val totalLikes: String,
    val totalSubscribers: String,
    val totalSubscriptions: String,
    val totalPublication: String,
    val avatar: ImageValue,
    val hasNft: Boolean,
)