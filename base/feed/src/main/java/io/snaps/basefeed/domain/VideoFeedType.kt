package io.snaps.basefeed.domain

import io.snaps.corecommon.model.Uuid

sealed interface VideoFeedType {

    object Main : VideoFeedType

    data class Single(val videoId: Uuid?) : VideoFeedType

    object UserLiked : VideoFeedType

    object Popular : VideoFeedType

    /** [userId]=null -> authed user */
    data class User(val userId: Uuid?) : VideoFeedType

    data class All(val query: String) : VideoFeedType

    object Subscriptions : VideoFeedType
}