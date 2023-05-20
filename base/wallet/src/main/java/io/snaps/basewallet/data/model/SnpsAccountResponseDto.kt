package io.snaps.basewallet.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SnpsAccountResponseDto(
    @SerialName("lockedTokensBalance") val lockedTokensBalance: Double,
    @SerialName("unlockedTokensBalance") val unlockedTokensBalance: Double,
    @SerialName("snpExchangeRate") val snpsExchangeRate: Double,
    @SerialName("bnbExchangeRate") val bnbExchangeRate: Double,
)