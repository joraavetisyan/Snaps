package io.snaps.basefeed.data

import io.snaps.basefeed.data.model.CommentResponseDto
import io.snaps.basefeed.data.model.UserLikedVideoFeedItemResponseDto
import io.snaps.basefeed.data.model.VideoFeedItemResponseDto
import io.snaps.basefeed.domain.CommentModel
import io.snaps.baseplayer.domain.VideoClipModel
import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.date.toOffsetLocalDateTime
import io.snaps.corecommon.model.Uuid
import java.time.ZonedDateTime

fun List<VideoFeedItemResponseDto>.toVideoClipModelList(
    likedVideos: List<UserLikedVideoFeedItemResponseDto>,
) = map { dto ->
    dto.toModel(likedVideos.firstOrNull { it.video.entityId == dto.entityId } != null)
}

fun VideoFeedItemResponseDto.toModel(isLiked: Boolean) = VideoClipModel(
    id = entityId,
    createdDate = createdDate,
    viewCount = viewsCount,
    commentCount = commentsCount,
    likeCount = likesCount,
    url = url,
    title = title,
    description = description,
    authorId = author.entityId,
    thumbnail = thumbnailUrl,
    isLiked = isLiked,
)

suspend fun List<CommentResponseDto>.toCommentModelList(
    owner: suspend (Uuid) -> UserInfoResponseDto?,
) = map { it.toModel(owner(it.userId)) }

fun CommentResponseDto.toModel(
    owner: UserInfoResponseDto?,
) = CommentModel(
    id = id,
    createdDate = requireNotNull(ZonedDateTime.parse(createdDate)).toOffsetLocalDateTime(),
    videoId = videoId,
    text = text,
    ownerImage = owner?.avatarUrl?.let(ImageValue::Url),
    ownerName = owner?.name.orEmpty(),

    isOwnerVerified = null,
    ownerTitle = null,
    likes = null,
    isLiked = null,
)