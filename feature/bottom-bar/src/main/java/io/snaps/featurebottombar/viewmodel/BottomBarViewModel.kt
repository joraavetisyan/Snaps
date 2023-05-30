package io.snaps.featurebottombar.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basenft.data.NftRepository
import io.snaps.basenft.domain.NftModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.data.model.Banner
import io.snaps.basesession.AppRouteProvider
import io.snaps.basesession.data.OnboardingHandler
import io.snaps.basesources.AppUpdateInfo
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
    onboardingHandler: OnboardingHandler,
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

    init {
        subscribeOnCountBrokenGlasses()

        bottomBarVisibilitySource.state.onEach { isBottomBarVisible ->
            _uiState.update { it.copy(isBottomBarVisible = isBottomBarVisible) }
        }.launchIn(viewModelScope)

        loadUserNft()
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

    private fun loadUserNft() = viewModelScope.launch {
        action.execute {
            nftRepository.updateNftCollection()
        }.doOnSuccess {
            loadBanner(it)
        }
    }

    fun updateMenuRoute(path: String?) {
        val route = path?.takeWhile { it != ROUTE_ARGS_SEPARATOR } ?: return
        appRouteProvider.updateMenuRouteState(route)
    }

    fun onOnboardingDialogActionClicked(type: OnboardingType?) {
        closeOnboardingDialog()
        when (type) {
            OnboardingType.Rank,
            OnboardingType.Nft -> {
                viewModelScope.launch {
                    _command publish Command.OpenNftPurchaseScreen
                }
            }
            OnboardingType.Popular,
            OnboardingType.Tasks,
            OnboardingType.Referral,
            OnboardingType.Wallet,
            OnboardingType.Rewards,
            null -> Unit
        }
    }

    fun setIsBannerShown() {
        _uiState.update {
            it.copy(
                isBannerShown = true,
                needShowBanner = true,
            )
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

    private fun loadBanner(nft: List<NftModel>) = viewModelScope.launch {
        profileRepository.getBanner().doOnSuccess { banner ->
            if (banner.isShown && userDataStorage.countBannerViews < 3
                && appRouteProvider.menuRouteState.value == AppRoute.MainBottomBar.MainTab1Start.path()
            ) {
                userDataStorage.countBannerViews += 1
                _uiState.update { it.copy(banner = banner) }
                if (nft.isNotEmpty()) {
                    _uiState.update { it.copy(needShowBanner = true) }
                    _command publish Command.ShowBottomDialog
                }
            }
        }
    }

    data class UiState(
        val isBottomBarVisible: Boolean = true,
        val badgeText: String = "",
        val appUpdateInfo: AppUpdateInfo? = null,
        val banner: Banner? = null,
        val isBannerShown: Boolean = false,
        val needShowBanner: Boolean = false,
    )

    sealed interface Command {
        object OpenNftPurchaseScreen : Command
        object ShowBottomDialog : Command
    }
}