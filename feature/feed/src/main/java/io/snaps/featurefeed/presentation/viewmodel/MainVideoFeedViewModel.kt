package io.snaps.featurefeed.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.data.CommentRepository
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.domain.VideoFeedInteractor
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.basefeed.ui.CreateCheckHandler
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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private fun SavedStateHandle.args() = getArg<AppRoute.SingleVideo.Args>()

@HiltViewModel
class MainVideoFeedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @Bridged mainHeaderHandler: MainHeaderHandler,
    @Bridged onboardingHandler: OnboardingHandler,
    bottomDialogBarVisibilityHandler: BottomDialogBarVisibilityHandler,
    createCheckHandler: CreateCheckHandler,
    action: Action,
    private val appRouteProvider: AppRouteProvider,
    videoFeedInteractor: VideoFeedInteractor,
    @Bridged private val videoFeedRepository: VideoFeedRepository,
    @Bridged private val profileRepository: ProfileRepository,
    @Bridged commentRepository: CommentRepository,
    @Bridged subsRepository: SubsRepository,
) : VideoFeedViewModel(
    bottomDialogBarVisibilityHandler = bottomDialogBarVisibilityHandler,
    videoFeedType = savedStateHandle.args()?.videoClipId?.let(VideoFeedType::Single) ?: VideoFeedType.Main,
    action = action,
    videoFeedInteractor = videoFeedInteractor,
    videoFeedRepository = videoFeedRepository,
    profileRepository = profileRepository,
    commentRepository = commentRepository,
    subsRepository = subsRepository,
), MainHeaderHandler by mainHeaderHandler,
    OnboardingHandler by onboardingHandler,
    CreateCheckHandler by createCheckHandler {

    private val args = savedStateHandle.args()

    private val _mainFeedState = MutableStateFlow(
        UiState(tab = Tab.Main.takeIf { args?.videoClipId == null })
    )
    val mainFeedState = _mainFeedState.asStateFlow()

    private val _mainFeedCommand = Channel<Command>()
    val mainFeedCommand = _mainFeedCommand.receiveAsFlow()

    init {
        viewModelScope.launch {
            checkOnboarding(OnboardingType.Rank)
        }
    }

    fun onTabRowClicked(tab: Tab) {
        _mainFeedState.update { it.copy(tab = tab) }
    }

    fun onCreateVideoClicked() {
        viewModelScope.launch { tryOpenCreate() }
    }

    data class UiState(
        val tab: Tab?,
    )

    enum class Tab(val label: TextValue) {
        Main(StringKey.MainVideoFeedTitleForYou.textValue()),
        Subscriptions(StringKey.MainVideoFeedTitleSubscriptions.textValue());
    }

    sealed class Command
}