package io.snaps.featurefeed.data.model

import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentResponseDto(
    @SerialName("id") val id: Uuid,
    @SerialName("ownerImage") val ownerImage: FullUrl,
    @SerialName("ownerName") val ownerName: String,
    @SerialName("text") val text: String,
    @SerialName("likes") val likes: Int,
    @SerialName("isLiked") val isLiked: Boolean,
    @SerialName("time") val time: String,
    @SerialName("isOwnerVerified") val isOwnerVerified: Boolean,
    @SerialName("ownerTitle") val ownerTitle: String?,
)