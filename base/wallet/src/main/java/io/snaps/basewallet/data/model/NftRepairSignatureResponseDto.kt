package io.snaps.basewallet.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NftRepairSignatureResponseDto(
    @SerialName("signature") val signature: String?,
    @SerialName("deadline") val deadline: Long?,
    @SerialName("amountReceiver") val amountReceiver: String?,
    @SerialName("profitWalelt" /*not a typo*/) val profitWallet: String?,
    @SerialName("contract") val contract: String?,
)