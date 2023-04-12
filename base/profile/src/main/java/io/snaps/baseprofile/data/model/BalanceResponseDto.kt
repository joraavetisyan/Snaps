package io.snaps.baseprofile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BalanceResponseDto(
    @SerialName("lockedTokensBalance") val lockedTokensBalance: Double,
    @SerialName("unlockedTokensBalance") val unlockedTokensBalance: Double,
    @SerialName("exchangeRate") val exchangeRate: Double,
)