package io.snaps.baseprofile.domain

data class InvitedReferralModel(
    val users: List<UserInfoModel>,
    val total: Int,
)