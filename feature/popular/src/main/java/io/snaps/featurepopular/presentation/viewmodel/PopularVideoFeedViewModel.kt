package io.snaps.featurepopular.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.basefeed.ui.VideoFeedViewModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesources.BottomBarVisibilitySource
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.basefeed.data.CommentRepository
import javax.inject.Inject

@HiltViewModel
class PopularVideoFeedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    action: Action,
    videoFeedRepository: VideoFeedRepository,
    profileRepository: ProfileRepository,
    commentRepository: CommentRepository,
    bottomBarVisibilitySource: BottomBarVisibilitySource,
) : VideoFeedViewModel(
    videoFeedType = VideoFeedType.Popular(savedStateHandle.requireArgs<AppRoute.PopularVideoFeed.Args>().query),
    startPosition = savedStateHandle.requireArgs<AppRoute.PopularVideoFeed.Args>().position,
    action = action,
    videoFeedRepository = videoFeedRepository,
    profileRepository = profileRepository,
    commentRepository = commentRepository,
    bottomBarVisibilitySource = bottomBarVisibilitySource,
)