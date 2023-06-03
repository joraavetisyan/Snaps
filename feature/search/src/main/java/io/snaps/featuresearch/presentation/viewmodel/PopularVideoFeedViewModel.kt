package io.snaps.featuresearch.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.data.CommentRepository
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.basefeed.ui.VideoFeedViewModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesources.BottomDialogBarVisibilityHandler
import io.snaps.basesubs.data.SubsRepository
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import javax.inject.Inject

@HiltViewModel
class PopularVideoFeedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    action: Action,
    @Bridged videoFeedRepository: VideoFeedRepository,
    @Bridged profileRepository: ProfileRepository,
    @Bridged commentRepository: CommentRepository,
    @Bridged subsRepository: SubsRepository,
    bottomDialogBarVisibilityHandler: BottomDialogBarVisibilityHandler,
) : VideoFeedViewModel(
    bottomDialogBarVisibilityHandler = bottomDialogBarVisibilityHandler,
    videoFeedType = savedStateHandle.requireArgs<AppRoute.PopularVideoFeed.Args>().query.let {
        if (it.isBlank()) VideoFeedType.Popular else VideoFeedType.Search(it)
    },
    action = action,
    videoFeedRepository = videoFeedRepository,
    profileRepository = profileRepository,
    commentRepository = commentRepository,
    subsRepository = subsRepository,
    startPosition = savedStateHandle.requireArgs<AppRoute.PopularVideoFeed.Args>().position,
)