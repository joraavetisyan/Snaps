package io.snaps.featureprofile

import io.snaps.baseprofile.domain.ProfileModel
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.featureprofile.screen.UserInfoTileState

fun State<ProfileModel>.toUserInfoTileState(
    onSubscribersClick: () -> Unit,
    onSubscriptionsClick: () -> Unit,
) = when (this) {
    is Loading -> UserInfoTileState.Shimmer
    is Effect -> when {
        isSuccess -> {
            requireData.toUserInfoTileState(
                onSubscribersClick = onSubscribersClick,
                onSubscriptionsClick = onSubscriptionsClick,
            )
        }
        else -> UserInfoTileState.Shimmer
    }
}

fun ProfileModel.toUserInfoTileState(
    onSubscribersClick: () -> Unit,
    onSubscriptionsClick: () -> Unit,
) = UserInfoTileState.Data(
    profileImage = avatar,
    likes = totalLikes,
    subscriptions = totalSubscriptions,
    subscribers = totalSubscribers,
    publication = totalPublication,
    onSubscribersClick = onSubscribersClick,
    onSubscriptionsClick = onSubscriptionsClick,
)