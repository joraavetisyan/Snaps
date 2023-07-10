package io.snaps.basenotifications.domain

import io.snaps.basenotifications.data.model.NotificationType
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.model.Uuid
import java.time.LocalDateTime

data class NotificationModel(
    val id: Uuid,
    val videoId: Uuid?,
    val ownerId: Uuid,
    val actionCreateUserId: Uuid,
    val actionCreateUserName: String,
    val actionCreateUserAvatar: ImageValue,
    val videoImage: ImageValue?,
    val text: String?,
    val type: NotificationType,
    val createdDate: LocalDateTime,
    val isSubscribed: Boolean = false,
)