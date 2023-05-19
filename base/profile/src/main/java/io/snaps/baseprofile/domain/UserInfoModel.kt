package io.snaps.baseprofile.domain

import io.snaps.baseprofile.data.model.PaymentsState
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.Uuid
import java.time.LocalDateTime

data class UserInfoModel(
    val entityId: Uuid,
    val createdDate: LocalDateTime,
    val userId: Uuid,
    val email: String?,
    val wallet: String?,
    val name: String,
    val totalLikes: Int,
    val totalSubscribers: Int,
    val totalSubscriptions: Int,
    val totalPublication: Int?,
    val avatarUrl: FullUrl?,
    val avatar: ImageValue,
    val experience: Int?,
    val level: Int?,
    val questInfo: QuestInfoModel?,
    val inviteCodeRegisteredBy: String?,
    val ownInviteCode: String?,
    val instagramId: Uuid?,
    val paymentsState: PaymentsState?,
    val firstLevelReferralMultiplier: Double,
    val secondLevelReferralMultiplier: Double,
)