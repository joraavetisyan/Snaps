package io.snaps.featurebottombar.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basenft.data.NftRepository
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.data.model.BannerDto
import io.snaps.basesession.AppRouteProvider
import io.snaps.basesession.data.OnboardingHandler
import io.snaps.basesources.AppUpdateInfoDto
import io.snaps.basesources.AppUpdateProvider
import io.snaps.basesources.BottomBarVisibilitySource
import io.snaps.basesources.UpdateAvailableState
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
    @Bridged onboardingHandler: OnboardingHandler,
    bottomBarVisibilitySource: BottomBarVisibilitySource,
    private val appRouteProvider: AppRouteProvider,
    private val appUpdateProvider: AppUpdateProvider,
    @Bridged private val nftRepository: NftRepository,
    @Bridged private val profileRepository: ProfileRepository,
    private val userDataStorage: UserDataStorage,
    private val action: Action,
) : SimpleViewModel(), OnboardingHandler by onboardingHandler {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    var isBannerShown: Boolean = false

    init {
        subscribeOnCountBrokenGlasses()

        bottomBarVisibilitySource.state.onEach { isBottomBarVisible ->
            _uiState.update { it.copy(isBottomBarVisible = isBottomBarVisible) }
        }.launchIn(viewModelScope)

        loadAppUpdateInfo()
    }

    private fun subscribeOnCountBrokenGlasses() = viewModelScope.launch {
        nftRepository.countBrokenGlassesState.onEach { count ->
            _uiState.update { state ->
                state.copy(
                    badgeText = count.dataOrCache?.takeIf { it > 0 }?.toString().orEmpty(),
                )
            }
        }.launchIn(viewModelScope)
    }

    fun updateMenuRoute(path: String?) {
        val route = path?.takeWhile { it != ROUTE_ARGS_SEPARATOR } ?: return
        appRouteProvider.updateMenuRouteState(route)
    }

    fun onOnboardingDialogActionClicked(type: OnboardingType?) {
        closeOnboardingDialog()
        when (type) {
            OnboardingType.Rank,
            OnboardingType.Nft -> viewModelScope.launch { _command publish Command.OpenNftPurchaseScreen }
            OnboardingType.Popular,
            OnboardingType.Tasks,
            OnboardingType.Referral,
            OnboardingType.Wallet,
            OnboardingType.Rewards,
            null -> Unit
        }
    }

    private fun loadAppUpdateInfo() = viewModelScope.launch {
        appUpdateProvider.getAvailableUpdateInfo().doOnSuccess { state ->
            if (state is UpdateAvailableState.Available) {
                _uiState.update { it.copy(appUpdateInfo = state.info) }
                _command publish Command.ShowBottomDialog
            }
        }
    }

    fun onCheckForBannerRequest() {
        _uiState.update { it.copy(bottomSheetShownCount = it.bottomSheetShownCount + 1) }
        if (_uiState.value.bottomSheetShownCount > 0 && !isBannerShown) {
            isBannerShown = true
            viewModelScope.launch {
                action.execute(needsErrorProcessing = false) {
                    profileRepository.getBanner().doOnSuccess { banner ->
                        if (banner.isViewable
                            && userDataStorage.countBannerViews++ < 3
                            && appRouteProvider.menuRouteState.value == AppRoute.MainBottomBar.MainTab1Start.path()
                        ) {
                            _uiState.update { it.copy(banner = banner) }
                            _command publish Command.ShowBottomDialog
                        }
                    }
                }
            }
        } else {
            _uiState.update { it.copy(banner = null) }
        }
    }

    data class UiState(
        val isBottomBarVisible: Boolean = true,
        val badgeText: String = "",
        val appUpdateInfo: AppUpdateInfoDto? = null,
        val bottomSheetShownCount: Int = 0,
        val banner: BannerDto? = null,
    )

    sealed interface Command {
        object OpenNftPurchaseScreen : Command
        object ShowBottomDialog : Command
    }
}