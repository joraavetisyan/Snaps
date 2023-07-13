package io.snaps.basenft.data.model

import io.snaps.corecommon.model.TxSign
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepairAllGlassesRequestDto(
    @SerialName("transactionData") val txSign: TxSign? = null, // transactionData not null for web3
)