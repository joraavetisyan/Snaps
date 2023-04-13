package io.snaps.baseprofile.data.model

import io.snaps.corecommon.model.DateTime
import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TransactionItemResponseDto(
    @SerialName("id") val id: Uuid,
    @SerialName("userId") val userId: Uuid,
    @SerialName("createdAt") val date: DateTime,
    @SerialName("type") val type: TransactionType,
    @SerialName("balanceChange") val balanceChange: Double,
)

@Serializable
enum class TransactionType {
    Withdrawal,
}