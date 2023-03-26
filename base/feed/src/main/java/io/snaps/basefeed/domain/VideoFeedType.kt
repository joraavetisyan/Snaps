package io.snaps.basefeed.domain

import io.snaps.corecommon.model.Uuid

sealed interface VideoFeedType {

    object Main : VideoFeedType

    object UserLiked : VideoFeedType

    data class Popular(val query: String) : VideoFeedType

    /** [userId]=null -> authed user */
    data class User(val userId: Uuid?) : VideoFeedType
}