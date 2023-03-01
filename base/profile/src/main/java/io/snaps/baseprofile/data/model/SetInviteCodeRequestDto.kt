package io.snaps.baseprofile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SetInviteCodeRequestDto(
    @SerialName("inviteCode") val inviteCode: String,
)