package io.snaps.basenft.data.model

import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.NftType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NftItemResponseDto(
    @SerialName("type") val type: NftType,
    @SerialName("dailyReward") val dailyReward: Int,
    @SerialName("costInUsd") val costInUsd: Int?,
    @SerialName("costInRealTokens") val costInRealTokens: Int?,
    @SerialName("isAvailableToPurchase") val isAvailableToPurchase: Boolean,
    @SerialName("pathToImage") val pathToImage: FullUrl,
    @SerialName("dailyMaintenanceCostMultiplier") val dailyMaintenanceCostMultiplier: Double?,
    @SerialName("percentGrowingPerDay") val percentGrowingPerDay: Double,
    @SerialName("additionalData") val additionalData: NftItemAdditionalDataDto?,
) {

    val repairCost: Double get() {
        val multiplier = dailyMaintenanceCostMultiplier ?: return 0.0
        return (dailyReward.toBigDecimal() * multiplier.toBigDecimal()).toDouble()
    }
}

@Serializable
data class NftItemAdditionalDataDto(
    @SerialName("leftCopies") val leftCopies: Int?,
)