package io.snaps.basenft.data.model

import io.snaps.corecommon.model.TxSign
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MintMysteryBoxRequestDto(
    @SerialName("type") val mysteryBoxType: Int,
    @SerialName("transactionData") val txSign: TxSign?,
    @SerialName("paymentType") val paymentType: Int,
    @SerialName("productId") val productId: String?,
    @SerialName("receipt") val receipt: String?,
)