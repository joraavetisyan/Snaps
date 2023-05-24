package io.snaps.basenft.data.model

import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.TxSign
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MintNftRequestDto(
    @SerialName("nftType") val nftType: NftType,
    @SerialName("transactionData") val txSign: TxSign?,
)