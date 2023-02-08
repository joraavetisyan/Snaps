package io.snaps.featurefeed.domain

import io.snaps.baseplayer.domain.VideoClipModel
import io.snaps.corecommon.model.AppError

const val VideoFeedPageSize = 3 // todo

data class VideoFeedPageModel(
    val loadedPageItems: List<VideoClipModel> = emptyList(),
    val nextPage: Int? = 0,
    val pageSize: Int = VideoFeedPageSize,
    val isLoading: Boolean = false,
    val error: AppError? = null,
)