package io.snaps.basewallet.data.model

import io.snaps.corecommon.model.CryptoAddress
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WalletSaveRequestDto(
    @SerialName("wallet") val address: CryptoAddress,
)