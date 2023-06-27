package io.snaps.baseprofile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InvitedReferralResponseDto(
    @SerialName("users") val users: List<UserInfoResponseDto>,
    @SerialName("total") val total: Int,
)