package io.snaps.basewallet.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NftSignatureRequestDto(
    @SerialName("nonce") val nonce: Long,
    @SerialName("amount") val amount: Double,
)