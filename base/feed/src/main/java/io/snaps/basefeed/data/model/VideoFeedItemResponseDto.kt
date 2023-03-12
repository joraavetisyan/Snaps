package io.snaps.basefeed.data.model

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
    @SerialName("description") val description: String,
    @SerialName("authorUserId") val authorUserId: Uuid,
)