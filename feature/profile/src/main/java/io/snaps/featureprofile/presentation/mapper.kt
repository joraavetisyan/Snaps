package io.snaps.featureprofile.presentation

import io.snaps.basenotifications.data.model.NotificationType
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.toCompactDecimalFormat
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.corecommon.strings.StringKey
import io.snaps.featureprofile.presentation.screen.UserInfoTileState
import io.snaps.featureprofile.presentation.viewmodel.Phrase

fun List<String>.toPhrases() = mapIndexed { i, s -> Phrase(orderNumber = i + 1, text = s) }

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
        else -> dataOrCache?.toUserInfoTileState(
            onSubscribersClick = onSubscribersClick,
            onSubscriptionsClick = onSubscriptionsClick,
        ) ?: UserInfoTileState.Shimmer
    }
}

fun UserInfoModel.toUserInfoTileState(
    onSubscribersClick: () -> Unit,
    onSubscriptionsClick: () -> Unit,
) = UserInfoTileState.Data(
    profileImage = avatar,
    likes = totalLikes.toCompactDecimalFormat(),
    subscriptions = totalSubscriptions,
    subscribers = totalSubscribers,
    publication = totalPublication?.toCompactDecimalFormat(),
    onSubscribersClick = onSubscribersClick,
    onSubscriptionsClick = onSubscriptionsClick,
)

fun NotificationType.toActionText(userName: String): TextValue {
    return when (this) {
        NotificationType.Like -> StringKey.NotificationsMessageLike.textValue(userName)
        NotificationType.Comment -> StringKey.NotificationsMessageComment.textValue(userName)
        NotificationType.Follow -> StringKey.NotificationsMessageFollow.textValue(userName)
    }
}