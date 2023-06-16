package io.snaps.basefeed.domain

import io.snaps.basefeed.data.model.VideoStatus
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.corecommon.model.DateTime
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.Uuid

data class VideoClipModel(
    val id: Uuid,
    val createdDate: DateTime,
    val viewCount: Int,
    val commentCount: Int,
    val likeCount: Int,
    val url: FullUrl,
    val title: String,
    val description: String?,
    val authorId: Uuid,
    val author: UserInfoModel?,
    val isLiked: Boolean,
    val thumbnail: FullUrl?,
    val status: VideoStatus?,
    val type: VideoFeedType,
)