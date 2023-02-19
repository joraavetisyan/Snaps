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
    @SerialName("email") val email: String,
    @SerialName("wallet") val wallet: WalletAddress,
    @SerialName("name") val name: String,
    @SerialName("totalLikes") val totalLikes: String,
    @SerialName("totalSubscribers") val totalSubscribers: String,
    @SerialName("totalSubscriptions") val totalSubscriptions: String,
    @SerialName("totalPublication") val totalPublication: String,
    @SerialName("avatar") val avatarUrl: FullUrl,
    @SerialName("hasNft") val hasNft: Boolean,
)