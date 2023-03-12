package io.snaps.basefeed.data.model

import io.snaps.corecommon.model.DateTime
import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentResponseDto(
    @SerialName("entityId") val id: Uuid,
    @SerialName("createdDate") val createdDate: DateTime,
    @SerialName("videoId") val videoId: Uuid,
    @SerialName("userId") val userId: Uuid,
    @SerialName("content") val text: String,
)