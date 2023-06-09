package io.snaps.basefeed.domain

import io.snaps.corecommon.model.Uuid

sealed interface VideoFeedType {

    object Main : VideoFeedType

    data class Single(val videoId: Uuid) : VideoFeedType

    /** [userId]=null -> of authed user */
    data class Liked(val userId: Uuid?) : VideoFeedType

    object Popular : VideoFeedType

    /** [userId]=null -> of authed user */
    data class User(val userId: Uuid?) : VideoFeedType

    data class Search(val query: String) : VideoFeedType

    object Subscriptions : VideoFeedType
}