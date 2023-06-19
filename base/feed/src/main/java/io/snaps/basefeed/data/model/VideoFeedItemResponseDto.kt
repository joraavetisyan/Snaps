package io.snaps.basefeed.data.model

import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.corecommon.model.DateTime
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoFeedItemResponseDto(
    @SerialName("entityId") val entityId: Uuid,
    @SerialName("internalId") val internalId: Uuid?,
    @SerialName("createdDate") val createdDate: DateTime,
    @SerialName("viewsCount") val viewsCount: Int,
    @SerialName("commentsCount") val commentsCount: Int,
    @SerialName("likesCount") val likesCount: Int,
    @SerialName("url") val url: FullUrl,
    @SerialName("thumbnailUrl") val thumbnailUrl: FullUrl?,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String?,
    @SerialName("author") val author: UserInfoResponseDto?, // null for my liked feed
    @SerialName("authorUserId") val authorId: Uuid?, // null for other feeds
    @SerialName("status") val status: VideoStatus? = null,
    @SerialName("isLiked") val isLiked: Boolean? = null, // null for my liked feed
)

@Serializable
enum class VideoStatus {
    @SerialName("Review") InReview,
    @SerialName("Reject") Rejected,
    @SerialName("Approved") Approved,
}