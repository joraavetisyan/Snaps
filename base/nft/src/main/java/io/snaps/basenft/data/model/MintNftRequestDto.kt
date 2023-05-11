package io.snaps.basenft.data.model

import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.Token
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MintNftRequestDto(
    @SerialName("type") val nftType: NftType,
    @SerialName("transactionId") val transactionHash: Token?,
)