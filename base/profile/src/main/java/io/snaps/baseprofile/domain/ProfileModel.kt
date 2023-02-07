package io.snaps.baseprofile.domain

import io.snaps.corecommon.container.ImageValue
import java.time.LocalDateTime

data class ProfileModel(
    val entityId: String,
    val createdDate: LocalDateTime,
    val userId: String,
    val email: String,
    val wallet: String,
    val name: String,
    val totalLikes: String,
    val totalSubscribers: String,
    val totalSubscriptions: String,
    val totalPublication: String,
    val avatar: ImageValue.Url,
)