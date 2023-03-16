package io.snaps.featurecollection.data.model

import io.snaps.corecommon.model.DateTime
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserNftItemResponseDto(
    @SerialName("id") val id: Uuid,
    @SerialName("userId") val userId: Uuid,
    @SerialName("tokenId") val tokenId: Uuid?,
    @SerialName("type") val type: NftItemResponseDto,
    @SerialName("mintedDate") val mintedDate: DateTime,
    @SerialName("isHealthy") val isHealthy: Boolean,
)