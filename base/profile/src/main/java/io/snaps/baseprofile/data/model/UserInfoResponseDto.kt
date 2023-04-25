package io.snaps.baseprofile.data.model

import io.snaps.corecommon.model.DateTime
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.model.WalletAddress
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class UserInfoResponseDto(
    @SerialName("entityId") val entityId: Uuid,
    @SerialName("createdDate") val createdDate: DateTime,
    @SerialName("userId") val userId: Uuid,
    @SerialName("email") val email: String?,
    @SerialName("wallet") val wallet: WalletAddress?,
    @SerialName("name") val name: String?,
    @SerialName("totalLikes") val totalLikes: Int,
    @SerialName("avatar") val avatarUrl: FullUrl?,
    @SerialName("experience") val experience: Int,
    @SerialName("questInfo") val questInfo: QuestInfoResponseDto?, // current quests aren't null only for current user
    @SerialName("subscribersCount") val totalSubscribers: Int,
    @SerialName("subscribesCount") val totalSubscriptions: Int,
    @SerialName("inviteCodeRegisteredBy") val inviteCodeRegisteredBy: String?,
    @SerialName("ownInviteCode") val ownInviteCode: String?,
    @SerialName("level") val level: Int,
    @SerialName("instagramId") val instagramId: Uuid?,
    @SerialName("paymentsState") val paymentsState: PaymentsState?,
)

enum class PaymentsState {
    No,
    InApp,
}