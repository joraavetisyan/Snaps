package io.snaps.baseprofile.data

import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.baseprofile.domain.ProfileModel
import io.snaps.baseprofile.ui.MainHeaderState
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.date.toOffsetLocalDateTime
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
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

fun State<ProfileModel>.toMainHeaderState(
    onProfileClicked: () -> Unit,
    onWalletClicked: () -> Unit,
): MainHeaderState = when (this) {
    is Effect -> if (isSuccess) MainHeaderState.Data(
        profileImage = requireData.avatar,
        // todo wallet state
        energy = "12",
        gold = "12",
        silver = "12",
        bronze = "12",
        onProfileClicked = onProfileClicked,
        onWalletClicked = onWalletClicked,
    ) else MainHeaderState.Error
    is Loading -> MainHeaderState.Shimmer
}