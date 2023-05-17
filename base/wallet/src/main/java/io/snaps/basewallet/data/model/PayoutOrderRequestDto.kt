package io.snaps.basewallet.data.model

import io.snaps.corecommon.model.CardNumber
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PayoutOrderRequestDto(
    @SerialName("cardNumber") val cardNumber: CardNumber,
    @SerialName("amount") val amount: Double,
)