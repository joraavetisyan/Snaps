package io.snaps.featureprofile

import io.snaps.baseprofile.domain.ProfileModel
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.featureprofile.screen.UserInfoTileState

fun State<ProfileModel>.toUserInfoTileState() = when (this) {
    is Loading -> UserInfoTileState.Shimmer
    is Effect -> when {
        isSuccess -> requireData.toUserInfoTileState()
        else -> UserInfoTileState.Shimmer
    }
}

fun ProfileModel.toUserInfoTileState() = UserInfoTileState.Data(
    profileImage = avatar,
    likes = totalLikes,
    subscriptions = totalSubscriptions,
    subscribers = totalSubscribers,
    publication = totalPublication,
)