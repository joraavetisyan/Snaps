package io.snaps.featurecollection.data.model

import io.snaps.corecommon.model.NftType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NftItemResponseDto(
    @SerialName("type") val type: NftType,
    @SerialName("dailyReward") val dailyReward: Int,
    @SerialName("percentGrowingPerDay") val dailyUnlock: Double,
    @SerialName("dailyMaintenanceCost") val dailyConsumption: Double,
)