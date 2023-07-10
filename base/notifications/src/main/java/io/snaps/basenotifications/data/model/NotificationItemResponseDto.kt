package io.snaps.basenotifications.data.model

import io.snaps.corecommon.model.DateTime
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationItemResponseDto(
    @SerialName("entityId") val entityId: Uuid,
    @SerialName("historyOwnerId") val historyOwnerId: Uuid,
    @SerialName("actionCreateUserId") val actionCreateUserId: Uuid,
    @SerialName("actionCreateUserName") val actionCreateUserName: String?,
    @SerialName("actionCreateUserAvatar") val actionCreateUserAvatar: FullUrl?,
    @SerialName("imageUrl") val imageUrl: FullUrl?, // thumbnails null for Follow
    @SerialName("videoId") val videoId: Uuid?, // videoId null for Follow
    @SerialName("text") val text: String?, // text not null for Comment
    @SerialName("type") val type: NotificationType,
    @SerialName("createdAt") val createdAt: DateTime,
)

@Serializable
enum class NotificationType {
    @SerialName("Like") Like,
    @SerialName("Follow") Follow,
    @SerialName("Comment") Comment,
}