package io.snaps.basefeed.data

import io.snaps.basefeed.data.model.CommentResponseDto
import io.snaps.basefeed.data.model.LikedVideoFeedItemResponseDto
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
    isExplicitlyLiked: Boolean = false,
    likedVideos: List<LikedVideoFeedItemResponseDto>,
) = map { dto -> dto.toModel(isExplicitlyLiked || likedVideos.find { it.video.entityId == dto.entityId } != null) }

fun VideoFeedItemResponseDto.toModel(isLiked: Boolean) = VideoClipModel(
    id = entityId,
    createdDate = createdDate,
    viewCount = viewsCount,
    commentCount = commentsCount,
    likeCount = likesCount,
    url = url,
    title = title,
    description = description,
    authorId = (author?.userId ?: authorId)!!,
    author = author?.toModel(),
    thumbnail = thumbnailUrl,
    isLiked = isLiked,
)

// Just a dummy wrapper
fun BaseResponse<List<LikedVideoFeedItemResponseDto>>.toFeedBaseResponse() = BaseResponse(
    data = data?.map { it.video },
    isSuccess = isSuccess,
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