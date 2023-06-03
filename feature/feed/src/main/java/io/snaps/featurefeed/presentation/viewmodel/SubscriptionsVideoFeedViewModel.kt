package io.snaps.featurefeed.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.data.CommentRepository
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.basefeed.ui.VideoFeedViewModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesession.AppRouteProvider
import io.snaps.basesources.BottomDialogBarVisibilityHandler
import io.snaps.basesubs.data.SubsRepository
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SubscriptionsVideoFeedViewModel @Inject constructor(
    bottomDialogBarVisibilityHandler: BottomDialogBarVisibilityHandler,
    action: Action,
    @Bridged videoFeedRepository: VideoFeedRepository,
    @Bridged profileRepository: ProfileRepository,
    @Bridged commentRepository: CommentRepository,
    @Bridged subsRepository: SubsRepository,
    private val appRouteProvider: AppRouteProvider,
) : VideoFeedViewModel(
    bottomDialogBarVisibilityHandler = bottomDialogBarVisibilityHandler,
    videoFeedType = VideoFeedType.Subscriptions,
    action = action,
    videoFeedRepository = videoFeedRepository,
    profileRepository = profileRepository,
    commentRepository = commentRepository,
    subsRepository = subsRepository,
) {

    init {
        subscribeOnMenuRouteState()
    }

    private fun subscribeOnMenuRouteState() {
        appRouteProvider.menuRouteState
            .filter { it == AppRoute.MainBottomBar.MainTab1Start.pattern }
            .onEach { onReloadClicked() }
            .launchIn(viewModelScope)
    }
}