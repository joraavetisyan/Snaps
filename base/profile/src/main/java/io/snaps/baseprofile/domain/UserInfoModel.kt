package io.snaps.baseprofile.domain

import io.snaps.baseprofile.data.model.PaymentsState
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.imageValue
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
    val experience: Int?,
    val level: Int?,
    val inviteCodeRegisteredBy: String?,
    val ownInviteCode: String?,
    val instagramId: Uuid?,
    val paymentsState: PaymentsState?,
    val firstLevelReferralMultiplier: Double,
    val secondLevelReferralMultiplier: Double,
    val isUsedTags: Boolean,
    val energy: Int,
) {

    val avatar: ImageValue get() = avatarUrl?.imageValue() ?: R.drawable.img_avatar.imageValue()
}