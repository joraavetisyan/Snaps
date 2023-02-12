package io.snaps.android.mainscreen

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.UserSessionTracker
import io.snaps.basesession.ActiveAppZoneProvider
import io.snaps.basesession.AppRouteProvider
import io.snaps.basesources.LocaleSource
import io.snaps.basesources.NotificationsSource
import io.snaps.corecommon.strings.StringHolder
import io.snaps.coredata.database.UserDataStorage
import io.snaps.coredata.database.WalletStorage
import io.snaps.corenavigation.base.ROUTE_ARGS_SEPARATOR
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.likeStateFlow
import io.snaps.coreuicompose.uikit.status.BannerMessage
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    localeSource: LocaleSource,
    activeAppZoneProvider: ActiveAppZoneProvider,
    userSessionTracker: UserSessionTracker,
    notificationsSource: NotificationsSource,
    private val userDataStorage: UserDataStorage,
    private val walletStorage: WalletStorage,
    private val appRouteProvider: AppRouteProvider,
) : SimpleViewModel() {

    val stringHolderState = localeSource.stateFlow
        .map { StringHolder(locale = it) }
        .likeStateFlow(viewModelScope, StringHolder())

    val currentFlowState = userSessionTracker.state.map { userSession ->
        when (userSession) {
            UserSessionTracker.State.NotActive -> StartFlow.RegistrationFlow(
                needsStartOnBoarding = !userDataStorage.isStartOnBoardingFinished,
            )
            UserSessionTracker.State.Active -> StartFlow.AuthorizedFlow(
                needsWalletConnect = walletStorage.wallet == null,
                needsInitialization = userDataStorage.userNameFlow.firstOrNull() == null,
                needsRanking = false,
            )
        }
    }.likeStateFlow(
        viewModelScope,
        StartFlow.RegistrationFlow(!userDataStorage.isStartOnBoardingFinished),
    )

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
                is StartFlow.RegistrationFlow -> ActiveAppZoneProvider.State.Registration
                is StartFlow.AuthorizedFlow -> ActiveAppZoneProvider.State.Authorized
            }
            activeAppZoneProvider.updateState(state)
        }.launchIn(viewModelScope)
    }

    fun updateAppRoute(path: String?) {
        path?.let { it ->
            val route = it.takeWhile { it != ROUTE_ARGS_SEPARATOR }
            appRouteProvider.updateState(route)
        }
    }

    sealed class StartFlow {

        data class RegistrationFlow(
            val needsStartOnBoarding: Boolean,
        ) : StartFlow()

        data class AuthorizedFlow(
            val needsWalletConnect: Boolean,
            val needsInitialization: Boolean,
            val needsRanking: Boolean,
        ) : StartFlow()
    }
}