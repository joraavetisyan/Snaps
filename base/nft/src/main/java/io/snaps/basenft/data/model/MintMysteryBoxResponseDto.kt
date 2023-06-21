package io.snaps.basenft.data.model

import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.TxHash
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MintMysteryBoxResponseDto(
    @SerialName("transactionId") val txHash: TxHash?,
    @SerialName("nftTypeFromBox") val nftTypeFromBox: NftType,
)