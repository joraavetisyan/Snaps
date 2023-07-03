package io.snaps.basefeed.data.model

import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MarkVideoShownRequestDto(
    @SerialName("videoId") val videoId: Uuid,
    @SerialName("videoDuration") val videoDuration: Float,
    @SerialName("duration") val duration: Float,
)
