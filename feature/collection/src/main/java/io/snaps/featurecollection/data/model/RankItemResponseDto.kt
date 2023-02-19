package io.snaps.featurecollection.data.model

import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RankItemResponseDto(
    @SerialName("id") val id: Uuid,
    @SerialName("type") val type: String,
    @SerialName("price") val price: String,
    @SerialName("image") val image: FullUrl,
    @SerialName("dailyReward") val dailyReward: String,
    @SerialName("dailyUnlock") val dailyUnlock: String,
    @SerialName("dailyConsumption") val dailyConsumption: String,
    @SerialName("isSelected") val isSelected: Boolean,
)