package io.snaps.basefeed.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Actually, no need for this wrapper
@Serializable
data class LikedVideoFeedItemResponseDto(
    /*@SerialName("entityId") val entityId: Uuid,*/
    @SerialName("videoDocument") val video: VideoFeedItemResponseDto,
)