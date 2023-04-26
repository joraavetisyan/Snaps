package io.snaps.featurefeed.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.data.CommentRepository
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.basefeed.ui.VideoFeedViewModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesession.data.OnboardingHandler
import io.snaps.basesources.BottomDialogBarVisibilityHandler
import io.snaps.corecommon.model.OnboardingType
import io.snaps.coredata.network.Action
import javax.inject.Inject

@HiltViewModel
class MainVideoFeedViewModel @Inject constructor(
    mainHeaderHandlerDelegate: MainHeaderHandler,
    onboardingHandlerDelegate: OnboardingHandler,
    bottomDialogBarVisibilityHandlerDelegate: BottomDialogBarVisibilityHandler,
    action: Action,
    videoFeedRepository: VideoFeedRepository,
    profileRepository: ProfileRepository,
    commentRepository: CommentRepository,
) : VideoFeedViewModel(
    videoFeedType = VideoFeedType.Main,
    action = action,
    videoFeedRepository = videoFeedRepository,
    profileRepository = profileRepository,
    commentRepository = commentRepository,
    bottomDialogBarVisibilityHandlerDelegate = bottomDialogBarVisibilityHandlerDelegate,
), MainHeaderHandler by mainHeaderHandlerDelegate, OnboardingHandler by onboardingHandlerDelegate {

    init {
        checkOnboarding(OnboardingType.Rank)
    }
}