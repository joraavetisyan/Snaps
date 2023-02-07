package io.snaps.baseprofile.data

import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.baseprofile.domain.ProfileModel
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.date.toOffsetLocalDateTime
import java.time.ZonedDateTime

fun UserInfoResponseDto.toProfileModel() = ProfileModel(
    entityId = entityId,
    createdDate = requireNotNull(ZonedDateTime.parse(createdDate)).toOffsetLocalDateTime(),
    userId = userId,
    email = email,
    wallet = wallet,
    name = name,
    totalLikes = totalLikes,
    totalSubscribers = totalSubscribers,
    totalSubscriptions = totalSubscriptions,
    totalPublication = totalPublication,
    avatar = ImageValue.Url(avatarUrl),
)