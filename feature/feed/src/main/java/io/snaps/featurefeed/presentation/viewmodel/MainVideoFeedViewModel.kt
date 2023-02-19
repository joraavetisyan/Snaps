package io.snaps.featurefeed.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.basefeed.ui.VideoFeedViewModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesources.BottomBarVisibilitySource
import io.snaps.coredata.network.Action
import io.snaps.basefeed.data.CommentRepository
import javax.inject.Inject

@HiltViewModel
class MainVideoFeedViewModel @Inject constructor(
    mainHeaderHandlerDelegate: MainHeaderHandler,
    action: Action,
    videoFeedRepository: VideoFeedRepository,
    profileRepository: ProfileRepository,
    commentRepository: CommentRepository,
    bottomBarVisibilitySource: BottomBarVisibilitySource,
) : VideoFeedViewModel(
    videoFeedType = VideoFeedType.Main,
    action = action,
    videoFeedRepository = videoFeedRepository,
    profileRepository = profileRepository,
    commentRepository = commentRepository,
    bottomBarVisibilitySource = bottomBarVisibilitySource,
), MainHeaderHandler by mainHeaderHandlerDelegate