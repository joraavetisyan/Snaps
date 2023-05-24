package io.snaps.featurefeed.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.data.CommentRepository
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.basefeed.ui.VideoFeedViewModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesources.BottomDialogBarVisibilityHandler
import io.snaps.basesources.featuretoggle.FeatureToggle
import io.snaps.basesubs.data.SubsRepository
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import javax.inject.Inject

@HiltViewModel
class SubscriptionsVideoFeedViewModel @Inject constructor(
    bottomDialogBarVisibilityHandler: BottomDialogBarVisibilityHandler,
    action: Action,
    @Bridged videoFeedRepository: VideoFeedRepository,
    @Bridged profileRepository: ProfileRepository,
    @Bridged commentRepository: CommentRepository,
    @Bridged subsRepository: SubsRepository,
    featureToggle: FeatureToggle,
) : VideoFeedViewModel(
    videoFeedType = VideoFeedType.Subscriptions,
    action = action,
    videoFeedRepository = videoFeedRepository,
    profileRepository = profileRepository,
    commentRepository = commentRepository,
    subsRepository = subsRepository,
    bottomDialogBarVisibilityHandler = bottomDialogBarVisibilityHandler,
    featureToggle = featureToggle,
)