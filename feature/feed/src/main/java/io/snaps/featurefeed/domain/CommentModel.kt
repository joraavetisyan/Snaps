package io.snaps.featurefeed.domain

import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.model.Uuid

data class CommentModel(
    val id: Uuid,
    val ownerImage: ImageValue,
    val ownerName: String,
    val text: String,
    val likes: Int,
    val isLiked: Boolean,
    val time: String,
    val isOwnerVerified: Boolean,
    val ownerTitle: String?,
)