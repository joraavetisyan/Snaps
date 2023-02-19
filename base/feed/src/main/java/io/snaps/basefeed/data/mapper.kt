package io.snaps.basefeed.data

import io.snaps.basefeed.data.model.CommentResponseDto
import io.snaps.basefeed.data.model.VideoFeedItemResponseDto
import io.snaps.basefeed.domain.CommentModel
import io.snaps.baseplayer.domain.VideoClipModel
import io.snaps.corecommon.container.ImageValue

fun List<VideoFeedItemResponseDto>.toVideoClipModelList() = map(VideoFeedItemResponseDto::toModel)

fun VideoFeedItemResponseDto.toModel() = VideoClipModel(
    id = entityId,
    createdDate = createdDate,
    viewCount = viewsCount,
    commentCount = commentsCount,
    likeCount = likesCount,
    url = url,
    title = title,
    description = description,
    authorId = authorUserId,
)

fun List<CommentResponseDto>.toCommentModelList() = map(CommentResponseDto::toModel)

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