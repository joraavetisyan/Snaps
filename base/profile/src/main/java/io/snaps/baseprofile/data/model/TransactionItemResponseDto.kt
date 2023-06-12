package io.snaps.baseprofile.data.model

import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.DateTime
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.strings.StringKey
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
enum class TransactionType {
    @SerialName("Withdrawal") Withdrawal,
    @SerialName("Send") Send,
    @SerialName("NftReward") NftReward,
    @SerialName("GlassesMaintaince") GlassesMaintaince,
    @SerialName("GasFee") GasFee,
    @SerialName("none") None,
    @SerialName("SubscribersPromo") SubscribersPromo,
    @SerialName("ViewsPromo") ViewsPromo,
    @SerialName("MoveToUnlock") MoveToUnlock,
    @SerialName("Reverse") Reverse,
    @SerialName("ReferralReward") ReferralReward,
    @SerialName("BloggerGlassesLikesPromo") BloggerGlassesLikesPromo,
    Unknown;

    val title: TextValue
        get() = when (this) {
            Withdrawal -> StringKey.TransactionTitleWithdrawal.textValue()
            Send -> StringKey.TransactionTitleSend.textValue()
            NftReward -> StringKey.TransactionTitleNftReward.textValue()
            GlassesMaintaince -> StringKey.TransactionTitleGlassesMaintaince.textValue()
            GasFee -> StringKey.TransactionTitleGasFee.textValue()
            None -> StringKey.TransactionTitleNone.textValue()
            SubscribersPromo -> StringKey.TransactionTitleSubscribersPromo.textValue()
            ViewsPromo -> StringKey.TransactionTitleViewsPromo.textValue()
            MoveToUnlock -> StringKey.TransactionTitleMoveToUnlock.textValue()
            Reverse -> StringKey.TransactionTitleReverse.textValue()
            ReferralReward -> StringKey.TransactionTitleReferralReward.textValue()
            BloggerGlassesLikesPromo -> StringKey.TransactionTitleBloggerGlassesLikesPromo.textValue()
            Unknown -> "Unknown".textValue()
        }
}