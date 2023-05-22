package io.snaps.basefeed.domain

import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.model.Uuid
import java.time.LocalDateTime

data class CommentModel(
    val id: Uuid,
    val videoId: Uuid,
    val owner: UserInfoModel?,
    val text: String,
    val createdDate: LocalDateTime,

    // not used for now
    val isOwnerVerified: Boolean?,
    val ownerTitle: String?,
    val isLiked: Boolean?,
    val likes: Int?,
)