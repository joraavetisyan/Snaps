package io.snaps.featurefeed.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.data.CommentRepository
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.basefeed.ui.VideoFeedViewModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesession.AppRouteProvider
import io.snaps.basesession.data.OnboardingHandler
import io.snaps.basesources.BottomDialogBarVisibilityHandler
import io.snaps.basesubs.data.SubsRepository
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.OnboardingType
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.getArg
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainVideoFeedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @Bridged mainHeaderHandler: MainHeaderHandler,
    onboardingHandler: OnboardingHandler,
    bottomDialogBarVisibilityHandler: BottomDialogBarVisibilityHandler,
    action: Action,
    @Bridged videoFeedRepository: VideoFeedRepository,
    @Bridged profileRepository: ProfileRepository,
    @Bridged commentRepository: CommentRepository,
    @Bridged subsRepository: SubsRepository,
    private val appRouteProvider: AppRouteProvider,
) : VideoFeedViewModel(
    bottomDialogBarVisibilityHandler = bottomDialogBarVisibilityHandler,
    videoFeedType = savedStateHandle.getArg<AppRoute.SingleVideo.Args>()?.videoClipId?.let {
        VideoFeedType.Single(it)
    } ?: VideoFeedType.Main,
    action = action,
    videoFeedRepository = videoFeedRepository,
    profileRepository = profileRepository,
    commentRepository = commentRepository,
    subsRepository = subsRepository,
), MainHeaderHandler by mainHeaderHandler, OnboardingHandler by onboardingHandler {

    private val args = savedStateHandle.getArg<AppRoute.SingleVideo.Args>()

    private val _screenState = MutableStateFlow(
        UiState(tab = Tab.Main.takeIf { args?.videoClipId == null })
    )
    val screenState = _screenState.asStateFlow()

    init {
        subscribeOnMenuRouteState()
        checkOnboarding(OnboardingType.Rank)
    }

    private fun subscribeOnMenuRouteState() {
        appRouteProvider.menuRouteState
            .filter { it == AppRoute.MainBottomBar.MainTab1Start.pattern }
            .onEach { onReloadClicked() }
            .launchIn(viewModelScope)
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