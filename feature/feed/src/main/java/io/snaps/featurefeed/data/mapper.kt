package io.snaps.featurefeed.data

import io.snaps.baseplayer.domain.VideoClipModel
import io.snaps.featurefeed.data.model.VideoFeedItemResponseDto

fun List<VideoFeedItemResponseDto>.toModelList() = map(VideoFeedItemResponseDto::toModel)

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