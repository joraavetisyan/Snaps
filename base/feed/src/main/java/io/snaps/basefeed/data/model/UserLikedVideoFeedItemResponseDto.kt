package io.snaps.basefeed.data.model

import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserLikedVideoFeedItemResponseDto(
    @SerialName("entityId") val entityId: Uuid,
    @SerialName("video") val video: VideoFeedItemResponseDto,
)