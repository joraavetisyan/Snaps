package io.snaps.featureprofile

import io.snaps.baseprofile.domain.ProfileModel
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.featureprofile.screen.UserInfoTileState

fun State<ProfileModel>.toUserInfoTileState() = when (this) {
    is Loading -> UserInfoTileState.Shimmer
    is Effect -> when {
        isSuccess -> UserInfoTileState.Data(
            profileImage = requireData.avatar,
            likes = requireData.totalLikes,
            subscriptions = requireData.totalSubscriptions,
            subscribers = requireData.totalSubscribers,
            publication = requireData.totalPublication,
        )
        else -> UserInfoTileState.Shimmer
    }
}