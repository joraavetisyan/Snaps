package io.snaps.basewallet.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClaimResponseDto(
    @SerialName("balance") val balance: Double?,
)