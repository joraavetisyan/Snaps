package io.snaps.basefeed.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateCommentRequestDto(
    @SerialName("content") val text: String,
)