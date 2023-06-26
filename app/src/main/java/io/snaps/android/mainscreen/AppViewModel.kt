package io.snaps.android.mainscreen

import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.snaps.android.appsflyer.DeepLinkSource
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesession.ActiveAppZoneProvider
import io.snaps.basesession.AppRouteProvider
import io.snaps.basesession.data.SessionRepository
import io.snaps.basesession.data.UserSessionTracker
import io.snaps.basesources.LocaleSource
import io.snaps.basesources.NotificationsSource
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringHolder
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.database.UserDataStorage
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.coredata.network.ApiService
import io.snaps.corenavigation.AppDeeplink
import io.snaps.corenavigation.Deeplink
import io.snaps.corenavigation.base.ROUTE_ARGS_SEPARATOR
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.likeStateFlow
import io.snaps.coreuicompose.uikit.status.BannerMessage
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AppViewModel @AssistedInject constructor(
    @Assisted private var deeplink: String?,
    localeSource: LocaleSource,
    activeAppZoneProvider: ActiveAppZoneProvider,
    userSessionTracker: UserSessionTracker,
    private val notificationsSource: NotificationsSource,
    @Bridged private val profileRepository: ProfileRepository,
    private val sessionRepository: SessionRepository,
    private val userDataStorage: UserDataStorage,
    private val appRouteProvider: AppRouteProvider,
    private val deepLinkSource: DeepLinkSource,
    private val apiService: ApiService,
    private val action: Action,
) : SimpleViewModel() {

    init {
        loadProdUrl()
    }

    private fun loadProdUrl() {
        viewModelScope.launch {
            if (apiService.getBaseUrl().isEmpty()) {
                apiService.loadProdUrl().doOnSuccess {
                    checkStatus()
                }
            } else {
                checkStatus()
            }
        }
    }

    private fun checkStatus() {
        viewModelScope.launch {
            sessionRepository.tryStatusCheck()
        }
    }

    val stringHolderState = localeSource.stateFlow
        .map { StringHolder(locale = it) }
        .likeStateFlow(viewModelScope, StringHolder())

    val currentFlowState = userSessionTracker.state.map { userSession ->
        when (userSession) {
            UserSessionTracker.State.Idle -> StartFlow.Idle
            UserSessionTracker.State.NotActive -> StartFlow.RegistrationFlow(
                needsStartOnBoarding = !userDataStorage.isStartOnBoardingFinished,
            )

            is UserSessionTracker.State.Active -> StartFlow.AuthorizedFlow(
                isError = userSession is UserSessionTracker.State.Active.Error,
                needsWalletConnect = userSession is UserSessionTracker.State.Active.NeedsWalletConnect,
                needsWalletImport = userSession is UserSessionTracker.State.Active.NeedsWalletImport,
                needsInitialization = userSession is UserSessionTracker.State.Active.NeedsInitialization,
                deeplink = AppDeeplink.parse(deeplink).also { deeplink = null },
            )
        }
    }.likeStateFlow(scope = viewModelScope, initialValue = StartFlow.Idle)

    val notificationsState = notificationsSource.state.map {
        when (it) {
            is NotificationsSource.State.Error -> BannerMessage.Error(it.value)
            is NotificationsSource.State.Message -> BannerMessage.Message(it.value)
            is NotificationsSource.State.Warning -> BannerMessage.Warning(it.value)
            null -> null
        }
    }.likeStateFlow(viewModelScope, null)

    init {
        currentFlowState.onEach {
            val state = when (it) {
                StartFlow.Idle -> ActiveAppZoneProvider.State.Idle
                is StartFlow.RegistrationFlow -> ActiveAppZoneProvider.State.Registration
                is StartFlow.AuthorizedFlow -> ActiveAppZoneProvider.State.Authorized
            }
            activeAppZoneProvider.updateState(state)
        }.launchIn(viewModelScope)
    }

    fun updateAppRoute(path: String?) {
        val route = path?.takeWhile { it != ROUTE_ARGS_SEPARATOR } ?: return
        appRouteProvider.updateAppRouteState(route)
    }

    fun onRetry() {
        loadProdUrl()
    }

    // If the application has not been installed, the first launch, then immediately apply the code after authorization
    fun applyReferralCode() {
        // checking if this is the first launch
        val appsFlyerDeepLink = deepLinkSource.state.value
        if (appsFlyerDeepLink !is AppDeeplink.Invite || deeplink != null) return
        viewModelScope.launch {
            action.execute(needsErrorProcessing = false) {
                profileRepository.setInviteCode(appsFlyerDeepLink.code)
            }.doOnSuccess {
                notificationsSource.sendMessage(StringKey.MessageReferralCodeApplySuccess.textValue())
            }
        }
    }

    sealed class StartFlow {

        object Idle : StartFlow()

        data class RegistrationFlow(
            val needsStartOnBoarding: Boolean,
        ) : StartFlow()

        // todo better way than multiple flags
        data class AuthorizedFlow(
            val isError: Boolean,
            val needsWalletConnect: Boolean,
            val needsWalletImport: Boolean,
            val needsInitialization: Boolean,
            val deeplink: Deeplink? = null,
        ) : StartFlow() {

            val isReady get() = !needsWalletConnect && !needsWalletImport && !needsInitialization
        }
    }
}