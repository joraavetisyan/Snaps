package io.snaps.basenft.data.model

import io.snaps.corecommon.model.NftTypeInt
import io.snaps.corecommon.model.Token
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MintNftRequestDto(
    @SerialName("nftType") val nftType: NftTypeInt,
    @SerialName("transactionId") val transactionHash: Token?,
)