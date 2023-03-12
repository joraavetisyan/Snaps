package io.snaps.featurecollection.data.model

import io.snaps.corecommon.model.NftTypeInt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MintNftRequestDto(
    @SerialName("nftType") val nftType: NftTypeInt,
)