package io.snaps.featurewallet.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BalanceResponseDto(
    @SerialName("lockedTokensBalance") val lockedTokensBalance: Int,
    @SerialName("unlockedTokensBalance") val unlockedTokensBalance: Int,
)