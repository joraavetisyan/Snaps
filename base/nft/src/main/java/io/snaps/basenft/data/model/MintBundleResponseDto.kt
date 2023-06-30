package io.snaps.basenft.data.model

import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.TxHash
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MintBundleResponseDto(
    @SerialName("transactionId") val txHash: TxHash?,
    @SerialName("nftTypeFromBundle") val nftTypeFromBundle: List<NftType>,
)