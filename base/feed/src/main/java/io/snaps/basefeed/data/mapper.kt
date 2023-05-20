package io.snaps.basefeed.data

import io.snaps.basefeed.data.model.CommentResponseDto
import io.snaps.basefeed.data.model.UserLikedVideoResponseDto
import io.snaps.basefeed.data.model.UserLikedVideoItem
import io.snaps.basefeed.data.model.VideoFeedItemResponseDto
import io.snaps.basefeed.domain.CommentModel
import io.snaps.baseplayer.domain.VideoClipModel
import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.date.toOffsetLocalDateTime
import io.snaps.corecommon.model.Uuid
import java.time.ZonedDateTime

fun List<VideoFeedItemResponseDto>.toVideoClipModelList(
    likedVideos: List<UserLikedVideoResponseDto>,
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
    authorId = author.userId,
    thumbnail = thumbnailUrl,
    isLiked = isLiked,
)

fun UserLikedVideoItem.toModel() = VideoClipModel(
    id = entityId,
    createdDate = createdDate,
    viewCount = viewsCount,
    commentCount = commentsCount,
    likeCount = likesCount,
    url = url,
    title = title,
    description = description,
    authorId = authorId,
    thumbnail = thumbnailUrl,
    isLiked = true,
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
    ownerImage = owner?.avatarUrl?.imageValue(),
    ownerName = owner?.name.orEmpty(),

    isOwnerVerified = null,
    ownerTitle = null,
    likes = null,
    isLiked = null,
)