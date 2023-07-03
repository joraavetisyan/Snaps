package io.snaps.basefeed.data

import io.snaps.basefeed.data.model.CommentResponseDto
import io.snaps.basefeed.data.model.LikedVideoFeedItemResponseDto
import io.snaps.basefeed.data.model.VideoFeedItemResponseDto
import io.snaps.basefeed.domain.CommentModel
import io.snaps.basefeed.domain.VideoClipModel
import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.baseprofile.data.toModel
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.basesettings.data.model.AdDto
import io.snaps.corecommon.date.toOffsetLocalDateTime
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.BaseResponse
import java.time.LocalDateTime
import java.time.ZonedDateTime

fun List<VideoFeedItemResponseDto>.toVideoClipModelList(
    isExplicitlyLiked: Boolean = false,
) = map { it.toModel(isExplicitlyLiked = isExplicitlyLiked) }

fun VideoFeedItemResponseDto.toModel(isExplicitlyLiked: Boolean) = VideoClipModel(
    id = entityId,
    viewCount = viewsCount,
    commentCount = commentsCount,
    likeCount = likesCount,
    url = url,
    title = title,
    description = description,
    authorId = (author?.userId ?: authorId)!!,
    author = author?.toModel(),
    thumbnail = thumbnailUrl,
    isSponsored = false,
    isCommentsAvailable = true,
    learnMoreLink = null,
    status = status,
    isLiked = isExplicitlyLiked || (isLiked ?: false),
    internalId = internalId,
)

fun AdDto.toVideoModel() = VideoClipModel(
    id = entityId,
    viewCount = 0,
    commentCount = 0,
    likeCount = 0,
    url = videoUrl,
    title = title,
    description = null,
    authorId = "",
    author = UserInfoModel(
        entityId = "",
        createdDate = LocalDateTime.now(),
        userId = "",
        email = "",
        wallet = "",
        name = username,
        totalLikes = 0,
        totalSubscribers = 0,
        totalSubscriptions = 0,
        totalPublication = 0,
        avatarUrl = avatar,
        experience = 0,
        level = 0,
        questInfo = null,
        inviteCodeRegisteredBy = null,
        ownInviteCode = null,
        instagramId = null,
        paymentsState = null,
        firstLevelReferralMultiplier = 0.0,
        secondLevelReferralMultiplier = 0.0,
        isUsedTags = true,
    ),
    isLiked = false,
    thumbnail = null,
    isSponsored = true,
    isCommentsAvailable = false,
    learnMoreLink = openUrl,
    status = null,
    internalId = "",
)

// Just a dummy wrapper
fun BaseResponse<VideoFeedItemResponseDto>.toFeedBaseResponse() = BaseResponse(
    data = listOfNotNull(data),
    isSuccess = isSuccess,
)

// Just a dummy wrapper
@JvmName("LikedVideosToFeedBaseResponse")
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