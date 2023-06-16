package io.snaps.basefeed.data

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.snaps.basefeed.data.model.VideoFeedItemResponseDto
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.basefeed.domain.VideoClipModel
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.coroutine.UserSessionCoroutineScope
import io.snaps.coredata.network.Action
import io.snaps.coredata.network.PagedLoader
import io.snaps.coredata.network.PagedLoaderFactory
import io.snaps.coredata.network.PagedLoaderParams
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

class VideoFeedLoader @AssistedInject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @UserSessionCoroutineScope private val scope: CoroutineScope,
    action: Action,
    @Assisted private val params: PagedLoaderParams<VideoFeedItemResponseDto, VideoClipModel>,
) : PagedLoader<VideoFeedItemResponseDto, VideoClipModel>(
    ioDispatcher = ioDispatcher,
    scope = scope,
    action = action,
    params = params,
)

@AssistedFactory
abstract class VideoFeedLoaderFactory :
    PagedLoaderFactory<VideoFeedType, VideoFeedLoader, VideoFeedItemResponseDto, VideoClipModel>() {

    override fun provide(params: PagedLoaderParams<VideoFeedItemResponseDto, VideoClipModel>) = create(params)

    abstract fun create(params: PagedLoaderParams<VideoFeedItemResponseDto, VideoClipModel>): VideoFeedLoader
}