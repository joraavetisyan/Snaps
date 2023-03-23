package io.snaps.featurecollection.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MintNftResponseDto(
    @SerialName("tokenId") val tokenId: Int?,
)