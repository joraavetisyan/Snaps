package io.snaps.featurefeed.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.data.CommentRepository
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.basefeed.ui.VideoFeedViewModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesession.data.OnboardingHandler
import io.snaps.basesources.BottomDialogBarVisibilityHandler
import io.snaps.basesubs.data.SubsRepository
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.OnboardingType
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.getArg
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainVideoFeedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    mainHeaderHandlerDelegate: MainHeaderHandler,
    onboardingHandlerDelegate: OnboardingHandler,
    bottomDialogBarVisibilityHandlerDelegate: BottomDialogBarVisibilityHandler,
    action: Action,
    videoFeedRepository: VideoFeedRepository,
    profileRepository: ProfileRepository,
    commentRepository: CommentRepository,
    subsRepository: SubsRepository,
) : VideoFeedViewModel(
    videoFeedType = savedStateHandle.getArg<AppRoute.SingleVideo.Args>()?.videoClipId?.let {
        VideoFeedType.Single(it)
    } ?: VideoFeedType.Main,
    action = action,
    videoFeedRepository = videoFeedRepository,
    profileRepository = profileRepository,
    commentRepository = commentRepository,
    subsRepository = subsRepository,
    bottomDialogBarVisibilityHandlerDelegate = bottomDialogBarVisibilityHandlerDelegate,
), MainHeaderHandler by mainHeaderHandlerDelegate, OnboardingHandler by onboardingHandlerDelegate {

    private val args = savedStateHandle.getArg<AppRoute.SingleVideo.Args>()

    private val _screenState = MutableStateFlow(
        UiState(tab = Tab.Main.takeIf { args?.videoClipId == null })
    )
    val screenState = _screenState.asStateFlow()

    init {
        checkOnboarding(OnboardingType.Rank)
    }

    fun onTabRowClicked(tab: Tab) {
        _screenState.update { it.copy(tab = tab) }
    }

    data class UiState(
        val tab: Tab?,
    )

    enum class Tab(val label: TextValue) {
        Main(StringKey.MainVideoFeedTitleForYou.textValue()),
        Subscriptions(StringKey.MainVideoFeedTitleSubscriptions.textValue());
    }
}