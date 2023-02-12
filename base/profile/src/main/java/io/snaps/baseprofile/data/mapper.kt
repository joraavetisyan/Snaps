package io.snaps.baseprofile.data

import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.baseprofile.domain.CoinsModel
import io.snaps.baseprofile.domain.ProfileModel
import io.snaps.baseprofile.ui.MainHeaderState
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.date.toOffsetLocalDateTime
import io.snaps.corecommon.model.Effect
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

fun mainHeaderState(
    profile: State<ProfileModel>,
    coins: State<CoinsModel>,
    onProfileClicked: () -> Unit,
    onWalletClicked: () -> Unit,
) = if (profile is Effect && coins is Effect) {
    if (profile.isSuccess && coins.isSuccess) {
        MainHeaderState.Data(
            profileImage = profile.requireData.avatar,
            energy = coins.requireData.energy,
            gold = coins.requireData.gold,
            silver = coins.requireData.silver,
            bronze = coins.requireData.bronze,
            onProfileClicked = onProfileClicked,
            onWalletClicked = onWalletClicked,
        )
    } else MainHeaderState.Error
} else {
    MainHeaderState.Shimmer
}