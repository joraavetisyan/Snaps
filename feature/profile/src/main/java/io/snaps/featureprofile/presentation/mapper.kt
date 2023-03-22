package io.snaps.featureprofile.presentation

import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.corecommon.ext.toFormatDecimal
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.featureprofile.presentation.screen.UserInfoTileState

fun State<UserInfoModel>.toUserInfoTileState(
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

fun UserInfoModel.toUserInfoTileState(
    onSubscribersClick: () -> Unit,
    onSubscriptionsClick: () -> Unit,
) = UserInfoTileState.Data(
    profileImage = avatar,
    likes = totalLikes.toFormatDecimal(),
    subscriptions = totalSubscriptions.toFormatDecimal(),
    subscribers = totalSubscribers.toFormatDecimal(),
    publication = totalPublication?.toFormatDecimal(),
    onSubscribersClick = onSubscribersClick,
    onSubscriptionsClick = onSubscriptionsClick,
)