package io.snaps.basewallet.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BundleSignatureRequestDto(
    @SerialName("nonce") val nonce: Long,
    @SerialName("amount") val amount: Double,
    @SerialName("type") val type: Int,
)