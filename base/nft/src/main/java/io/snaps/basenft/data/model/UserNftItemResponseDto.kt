package io.snaps.basenft.data.model

import io.snaps.corecommon.model.DateTime
import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserNftItemResponseDto(
    @SerialName("id") val id: Uuid,
    @SerialName("userId") val userId: Uuid,
    @SerialName("tokenId") val tokenId: Uuid?,
    @SerialName("type") val data: NftItemResponseDto,
    @SerialName("mintedDate") val mintedDate: DateTime,
    @SerialName("isHealthy") val isHealthy: Boolean,
    @SerialName("levelInfo") val levelInfo: LevelInfo,
)

@Serializable
data class LevelInfo(
    @SerialName("level") val level: Int,
    @SerialName("experience") val experience: Int,
    @SerialName("lowerThreshold") val lowerThreshold: Int,
    @SerialName("upperThreshold") val upperThreshold: Int,
    @SerialName("bonus") val bonus: Int,
)