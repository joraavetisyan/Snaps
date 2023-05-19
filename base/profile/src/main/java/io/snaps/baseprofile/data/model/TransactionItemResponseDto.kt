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
    @SerialName("type") val type: TransactionType = TransactionType.Unknown,
    @SerialName("balanceChange") val balanceChange: Double,
)

@Serializable
enum class TransactionType(val title: String) {
    @SerialName("Withdrawal") Withdrawal("Withdrawal"),
    @SerialName("Send") Send("Replenishment"),
    @SerialName("NftReward") NftReward("Nft reward"),
    @SerialName("GlassesMaintaince") GlassesMaintaince("Glass repair"),
    @SerialName("GasFee") GasFee("Gas Fee"),
    @SerialName("none") None("Transaction"),
    @SerialName("SubscribersPromo") SubscribersPromo("Subscribers Promo"),
    @SerialName("ViewsPromo") ViewsPromo("Views Promo"),
    @SerialName("MoveToUnlock") MoveToUnlock("Move to unlock"),
    Unknown("Unknown"),
}