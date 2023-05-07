package io.snaps.basenft.data.model

import io.snaps.corecommon.model.NftTypeInt
import io.snaps.corecommon.model.Token
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.model.WalletAddress
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MintNftRequestDto(
    @SerialName("nftType") val nftType: NftTypeInt,
    @SerialName("purchaseId") val transactionHash: Token?,
)