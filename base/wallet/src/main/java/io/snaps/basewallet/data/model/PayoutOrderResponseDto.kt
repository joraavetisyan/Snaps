package io.snaps.basewallet.data.model

import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PayoutOrderResponseDto(
    @SerialName("id") val id: Uuid,
    @SerialName("entityId") val entityId: Uuid,
    @SerialName("status") val status: PayoutOrderStatus,
)

@Serializable
enum class PayoutOrderStatus {
    @SerialName("InProcess") InProcess,
    @SerialName("Success") Success,
    @SerialName("Rejected") Rejected,
}