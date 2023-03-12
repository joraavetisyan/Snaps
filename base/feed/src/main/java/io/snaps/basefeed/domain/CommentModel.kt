package io.snaps.basefeed.domain

import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.model.Uuid
import java.time.LocalDateTime

data class CommentModel(
    val id: Uuid,
    val videoId: Uuid,
    val ownerImage: ImageValue?,
    val ownerName: String,
    val text: String,
    val createdDate: LocalDateTime,

    val isOwnerVerified: Boolean?,
    val ownerTitle: String?,
    val isLiked: Boolean?,
    val likes: Int?,
)