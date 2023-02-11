package io.snaps.featurefeed.data

import io.snaps.corecommon.container.ImageValue
import io.snaps.featurefeed.data.model.CommentResponseDto
import io.snaps.featurefeed.domain.CommentModel

fun List<CommentResponseDto>.toModelList() = map(CommentResponseDto::toModel)

fun CommentResponseDto.toModel() = CommentModel(
    id = id,
    ownerImage = ImageValue.Url(ownerImage),
    ownerName = ownerName,
    text = text,
    likes = likes,
    isLiked = isLiked,
    time = time,
    isOwnerVerified = isOwnerVerified,
    ownerTitle = ownerTitle,
)