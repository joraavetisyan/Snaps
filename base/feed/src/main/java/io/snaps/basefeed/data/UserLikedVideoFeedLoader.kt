package io.snaps.basefeed.data

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.snaps.basefeed.data.model.UserLikedVideoResponseDto
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.baseplayer.domain.VideoClipModel
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.Action
import io.snaps.coredata.network.PagedLoader
import io.snaps.coredata.network.PagedLoaderFactory
import io.snaps.coredata.network.PagedLoaderParams
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

class UseLikedVideoFeedLoader @AssistedInject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    action: Action,
    @Assisted private val params: PagedLoaderParams<UserLikedVideoResponseDto, VideoClipModel>,
) : PagedLoader<UserLikedVideoResponseDto, VideoClipModel>(
    ioDispatcher = ioDispatcher,
    scope = scope,
    action = action,
    params = params,
)

@AssistedFactory
abstract class UserLikedVideoFeedLoaderFactory : PagedLoaderFactory<VideoFeedType.Liked, UseLikedVideoFeedLoader, UserLikedVideoResponseDto, VideoClipModel>() {

    override fun provide(params: PagedLoaderParams<UserLikedVideoResponseDto, VideoClipModel>) = create(params)

    abstract fun create(params: PagedLoaderParams<UserLikedVideoResponseDto, VideoClipModel>): UseLikedVideoFeedLoader
}