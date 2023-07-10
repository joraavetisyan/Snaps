package io.snaps.featureprofile.presentation

import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.corecommon.ext.toCompactDecimalFormat
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.featureprofile.presentation.screen.UserInfoTileState
import io.snaps.featureprofile.presentation.viewmodel.Phrase

fun List<String>.toPhrases() = mapIndexed { i, s -> Phrase(orderNumber = i + 1, text = s) }

fun State<UserInfoModel>.toUserInfoTileState(
    onSubscribersClick: () -> Unit,
    onSubscriptionsClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    isUserCurrent: Boolean
) = when (this) {
    is Loading -> UserInfoTileState.Shimmer
    is Effect -> when {
        isSuccess -> {
            requireData.toUserInfoTileState(
                onSubscribersClick = onSubscribersClick,
                onSubscriptionsClick = onSubscriptionsClick,
                isUserCurrent = isUserCurrent,
                onEditProfileClick = onEditProfileClick
            )
        }
        else -> dataOrCache?.toUserInfoTileState(
            onSubscribersClick = onSubscribersClick,
            onSubscriptionsClick = onSubscriptionsClick,
            isUserCurrent = isUserCurrent,
            onEditProfileClick = onEditProfileClick
        ) ?: UserInfoTileState.Shimmer
    }
}

fun UserInfoModel.toUserInfoTileState(
    onSubscribersClick: () -> Unit,
    onSubscriptionsClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    isUserCurrent: Boolean
) = UserInfoTileState.Data(
    profileImage = avatar,
    profileTitle = name,
    isUserCurrent = isUserCurrent,
    likes = totalLikes.toCompactDecimalFormat(),
    subscriptions = totalSubscriptions,
    subscribers = totalSubscribers,
    publication = totalPublication?.toCompactDecimalFormat(),
    onSubscribersClick = onSubscribersClick,
    onSubscriptionsClick = onSubscriptionsClick,
    onEditProfileClick = onEditProfileClick
)