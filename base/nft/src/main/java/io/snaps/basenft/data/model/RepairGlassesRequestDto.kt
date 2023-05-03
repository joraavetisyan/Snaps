package io.snaps.basenft.data.model

import io.snaps.corecommon.model.Token
import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepairGlassesRequestDto(
    @SerialName("glassesId") val glassesId: Uuid,
    @SerialName("transactionId") val transactionHash: Token? = null, // blockchain repair tx hash
    @SerialName("offChainAmount") val offChainAmount: Long = 0L,
)