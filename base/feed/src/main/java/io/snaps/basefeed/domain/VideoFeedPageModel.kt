package io.snaps.basefeed.domain

import io.snaps.baseplayer.domain.VideoClipModel
import io.snaps.corecommon.model.AppError

data class VideoFeedPageModel(
    val pageSize: Int,
    val loadedPageItems: List<VideoClipModel> = emptyList(),
    val nextPage: Int? = 0,
    val isLoading: Boolean = false,
    val error: AppError? = null,
)