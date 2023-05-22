package io.snaps.basenft.data.model

import io.snaps.corecommon.model.Token
import io.snaps.corecommon.model.TxSign
import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MintNftStoreRequestDto(
    @SerialName("productId") val productId: Uuid,
    @SerialName("purchaseId") val purchaseToken: Token,
    @SerialName("transactionData") val txSign: TxSign,
)