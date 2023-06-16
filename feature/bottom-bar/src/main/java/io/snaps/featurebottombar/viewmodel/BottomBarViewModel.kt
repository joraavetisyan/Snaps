package io.snaps.featurebottombar.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basenft.data.NftRepository
import io.snaps.basesession.AppRouteProvider
import io.snaps.basesession.data.OnboardingHandler
import io.snaps.basesettings.data.SettingsRepository
import io.snaps.basesettings.data.AppUpdateProvider
import io.snaps.basesources.BottomBarVisibilitySource
import io.snaps.basesettings.data.UpdateAvailableState
import io.snaps.basesources.remotedata.model.AppUpdateInfoDto
import io.snaps.basesources.remotedata.model.BannerActionType
import io.snaps.basesources.remotedata.model.BannerDto
import io.snaps.corecommon.date.CountdownTimer
import io.snaps.corecommon.date.toTimeFormat
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.OnboardingType
import io.snaps.coredata.database.UserDataStorage
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.ROUTE_ARGS_SEPARATOR
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomBarViewModel @Inject constructor(
    bottomBarVisibilitySource: BottomBarVisibilitySource,
    @Bridged onboardingHandler: OnboardingHandler,
    @Bridged private val nftRepository: NftRepository,
    private val settingsRepository: SettingsRepository,
    private val appRouteProvider: AppRouteProvider,
    private val appUpdateProvider: AppUpdateProvider,
    private val userDataStorage: UserDataStorage,
    private val action: Action,
) : SimpleViewModel(), OnboardingHandler by onboardingHandler {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private val bannerTimer = CountdownTimer()
    private var isBannerShown: Boolean = false

    init {
        subscribeOnCountBrokenGlasses()
        subscribeOnAppUpdateInfo()

        bottomBarVisibilitySource.state.onEach { isBottomBarVisible ->
            _uiState.update { it.copy(isBottomBarVisible = isBottomBarVisible) }
        }.launchIn(viewModelScope)

        loadSettings()
    }

    private fun subscribeOnCountBrokenGlasses() {
        nftRepository.countBrokenGlassesState.onEach { count ->
            _uiState.update { state ->
                state.copy(
                    badgeText = count.dataOrCache?.takeIf { it > 0 }?.toString().orEmpty(),
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun subscribeOnAppUpdateInfo() {
        appUpdateProvider.state.onEach { appUpdateState ->
            if (appUpdateState is UpdateAvailableState.Available) {
                _uiState.update { it.copy(appUpdateInfo = appUpdateState.info) }
                _command publish Command.ShowBottomDialog
            }
        }.launchIn(viewModelScope)
    }

    private fun subscribeOnBannerState() {
        settingsRepository.bannerState.onEach { state ->
            if (state is Effect && state.isSuccess) {
                val banner = requireNotNull(state.requireData)
                if (banner.isViewable
                    && (banner.isEndless || userDataStorage.countBannerViews++ < 3)
                    && appRouteProvider.menuRouteState.value == AppRoute.MainBottomBar.MainTab1Start.path()
                ) {
                    if (banner.isTimerShown) {
                        launchBannerTimer()
                    }
                    _uiState.update { it.copy(banner = banner) }
                    _command publish Command.ShowBottomDialog
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateMenuRoute(path: String?) {
        val route = path?.takeWhile { it != ROUTE_ARGS_SEPARATOR } ?: return
        appRouteProvider.updateMenuRouteState(route)
    }

    fun onOnboardingDialogActionClicked(type: OnboardingType?) {
        viewModelScope.launch {
            closeOnboardingDialog()
            when (type) {
                OnboardingType.Rank,
                OnboardingType.Nft -> _command publish Command.OpenNftPurchaseScreen
                OnboardingType.Popular,
                OnboardingType.Tasks,
                OnboardingType.Referral,
                OnboardingType.Wallet,
                OnboardingType.Rewards,
                null -> Unit
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            action.execute { settingsRepository.update() }
        }
    }

    fun onCheckForBannerRequest() {
        bannerTimer.stop()
        if (!isBannerShown) {
            isBannerShown = true
            subscribeOnBannerState()
        } else {
            _uiState.update { it.copy(banner = null) }
        }
    }

    private fun launchBannerTimer() {
        viewModelScope.launch {
            action.execute(needsErrorProcessing = false) {
                settingsRepository.getCommonSettings()
            }.doOnSuccess { settings ->
                bannerTimer.start(
                    scope = viewModelScope,
                    tickUntil = settings.likerGlassesReleaseDate,
                    onTick = { leftTime ->
                        _uiState.update { it.copy(bannerTimer = leftTime.toTimeFormat()) }
                    },
                    onFinished = {
                        _uiState.update { it.copy(bannerTimer = null) }
                    },
                )
            }
        }
    }

    fun onBannerActionClicked(banner: BannerDto) {
        viewModelScope.launch {
            _command publish Command.HideBottomDialog
            if (banner.actionType == BannerActionType.NftList) {
                _command publish Command.OpenNftPurchaseScreen
            } else {
                _command publish Command.OpenUrlScreen(banner.action)
            }
        }
    }

    data class UiState(
        val isBottomBarVisible: Boolean = true,
        val badgeText: String = "",
        val appUpdateInfo: AppUpdateInfoDto? = null,
        val banner: BannerDto? = null,
        val bannerTimer: String? = null,
    )

    sealed interface Command {
        object OpenNftPurchaseScreen : Command
        data class OpenUrlScreen(val url: FullUrl) : Command
        object ShowBottomDialog : Command
        object HideBottomDialog : Command
    }
}