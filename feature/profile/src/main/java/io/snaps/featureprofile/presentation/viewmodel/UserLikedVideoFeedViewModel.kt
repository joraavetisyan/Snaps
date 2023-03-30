package io.snaps.featureprofile.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.data.CommentRepository
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.basefeed.ui.VideoFeedViewModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesources.BottomBarVisibilitySource
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import javax.inject.Inject

@HiltViewModel
class UserLikedVideoFeedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    action: Action,
    videoFeedRepository: VideoFeedRepository,
    profileRepository: ProfileRepository,
    commentRepository: CommentRepository,
    bottomBarVisibilitySource: BottomBarVisibilitySource,
) : VideoFeedViewModel(
    videoFeedType = VideoFeedType.UserLiked,
    startPosition = savedStateHandle.requireArgs<AppRoute.UserLikedVideoFeed.Args>().position,
    action = action,
    videoFeedRepository = videoFeedRepository,
    profileRepository = profileRepository,
    commentRepository = commentRepository,
    bottomBarVisibilitySource = bottomBarVisibilitySource,
)