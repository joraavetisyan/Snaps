package io.snaps.basenotifications.data

import io.snaps.basenotifications.data.model.NotificationItemResponseDto
import io.snaps.basenotifications.domain.NotificationModel
import io.snaps.corecommon.R
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.date.toOffsetLocalDateTime
import java.time.ZonedDateTime

fun List<NotificationItemResponseDto>.toModelList() = map(NotificationItemResponseDto::toModel)

fun NotificationItemResponseDto.toModel() = NotificationModel(
    id = entityId,
    videoId = videoId,
    ownerId = historyOwnerId,
    actionCreateUserId = actionCreateUserId,
    actionCreateUserName = actionCreateUserName ?: "Snaps User",
    actionCreateUserAvatar = actionCreateUserAvatar?.imageValue() ?: R.drawable.img_avatar.imageValue(),
    videoImage = imageUrl?.imageValue(),
    text = text,
    type = type,
    createdDate = requireNotNull(ZonedDateTime.parse(createdAt)).toOffsetLocalDateTime(),
)