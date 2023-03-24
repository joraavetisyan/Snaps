package io.snaps.basenft.data.model

import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.NftType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NftItemResponseDto(
    @SerialName("type") val type: NftType,
    @SerialName("dailyReward") val dailyReward: Int,
    @SerialName("percentGrowingPerDay") val dailyUnlock: Double,
    @SerialName("dailyMaintenanceCostMultiplier") val dailyConsumption: Double,
    @SerialName("costInUsd") val costInUsd: Int?,
    @SerialName("costInRealTokens") val costInRealTokens: Int?,
    @SerialName("isAvailableToPurchase") val isAvailableToPurchase: Boolean,
    @SerialName("pathToImage") val pathToImage: FullUrl,
)