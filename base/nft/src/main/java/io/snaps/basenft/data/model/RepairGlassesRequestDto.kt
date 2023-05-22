package io.snaps.basenft.data.model

import io.snaps.corecommon.model.TxSign
import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepairGlassesRequestDto(
    @SerialName("glassesId") val glassesId: Uuid,
    @SerialName("transactionData") val txSign: TxSign? = null,
)