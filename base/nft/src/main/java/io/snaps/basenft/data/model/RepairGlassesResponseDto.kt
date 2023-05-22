package io.snaps.basenft.data.model

import io.snaps.corecommon.model.TxHash
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepairGlassesResponseDto(
    @SerialName("transactionId") val transactionId: TxHash?,
)