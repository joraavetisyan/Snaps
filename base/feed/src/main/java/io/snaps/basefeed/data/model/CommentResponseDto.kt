package io.snaps.basefeed.data.model

import io.snaps.corecommon.model.DateTime
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentResponseDto(
    @SerialName("entityId") val id: Uuid,
    @SerialName("videoId") val videoId: Uuid,
    @SerialName("ownerImage") val ownerImage: FullUrl,
    @SerialName("ownerName") val ownerName: String,
    @SerialName("content") val text: String,
    @SerialName("likes") val likes: Int,
    @SerialName("isLiked") val isLiked: Boolean,
    @SerialName("isOwnerVerified") val isOwnerVerified: Boolean,
    @SerialName("ownerTitle") val ownerTitle: String?,
    @SerialName("createdDate") val createdDate: DateTime,
)