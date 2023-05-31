package io.snaps.basefeed.data

import io.snaps.basefeed.data.model.CommentResponseDto
import io.snaps.basefeed.data.model.UserLikedVideoResponseDto
import io.snaps.basefeed.data.model.UserLikedVideoItem
import io.snaps.basefeed.data.model.VideoFeedItemResponseDto
import io.snaps.basefeed.domain.CommentModel
import io.snaps.basefeed.domain.VideoClipModel
import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.baseprofile.data.toModel
import io.snaps.corecommon.date.toOffsetLocalDateTime
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.BaseResponse
import java.time.ZonedDateTime

fun List<VideoFeedItemResponseDto>.toVideoClipModelList(
    likedVideos: List<UserLikedVideoResponseDto>,
) = map { dto -> dto.toModel(likedVideos.find { it.video.entityId == dto.entityId } != null) }

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
    author = author.toModel(),
    thumbnail = thumbnailUrl,
    isLiked = isLiked,
)

// Just a dummy wrapper
fun BaseResponse<List<UserLikedVideoItem>>.toLikedFeedBaseResponse() = BaseResponse(
    data = data?.map { UserLikedVideoResponseDto(it) },
    isSuccess = isSuccess,
)

fun List<UserLikedVideoResponseDto>.likedFeedToVideoClipModelList(
    isExplicitlyLiked: Boolean,
    likedVideos: List<UserLikedVideoResponseDto>,
) = map { dto ->
    dto.video.toModel(
        isLiked = isExplicitlyLiked || likedVideos.find { it.video.entityId == dto.video.entityId } != null
    )
}

fun UserLikedVideoItem.toModel(isLiked: Boolean) = VideoClipModel(
    id = entityId,
    createdDate = createdDate,
    viewCount = viewsCount,
    commentCount = commentsCount,
    likeCount = likesCount,
    url = url,
    title = title,
    description = description,
    authorId = authorId,
    author = null,
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
    owner = owner?.toModel(),

    isOwnerVerified = null,
    ownerTitle = null,
    likes = null,
    isLiked = null,
)