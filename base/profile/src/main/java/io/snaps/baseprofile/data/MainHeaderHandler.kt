package io.snaps.baseprofile.data

import io.snaps.baseprofile.ui.MainHeaderState
import io.snaps.basewallet.data.WalletRepository
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.network.Action
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

interface MainHeaderHandler {

    val headerUiState: StateFlow<UiState>

    val headerCommand: Flow<Command>

    data class UiState(
        val value: MainHeaderState = MainHeaderState.Shimmer,
    )

    sealed interface Command {
        object OpenProfileScreen : Command
        object OpenWalletScreen : Command
    }
}

class MainHeaderHandlerImplDelegate @Inject constructor(
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    private val action: Action,
    private val profileRepository: ProfileRepository,
    private val walletRepository: WalletRepository,
) : MainHeaderHandler {

    private val _uiState = MutableStateFlow(MainHeaderHandler.UiState())
    override val headerUiState = _uiState.asStateFlow()

    private val _command = Channel<MainHeaderHandler.Command>()
    override val headerCommand = _command.receiveAsFlow()

    init {
        walletRepository.getBnbWalletModel()
        subscribeToData()
        updateData()
    }

    private fun subscribeToData() {
        profileRepository.state.combine(flow = walletRepository.activeWallets) { profile, _ ->
            mainHeaderState(
                profile = profile,
                snp = walletRepository.getSnpWalletModel(),
                bnb = walletRepository.getBnbWalletModel(),
                onProfileClicked = ::onProfileClicked,
                onWalletClicked = ::onWalletClicked,
            )
        }.onEach { state ->
            _uiState.update { it.copy(value = state) }
        }.launchIn(scope)
    }

    private fun onProfileClicked() {
        _command.trySend(MainHeaderHandler.Command.OpenProfileScreen)
    }

    private fun onWalletClicked() {
        _command.trySend(MainHeaderHandler.Command.OpenWalletScreen)
    }

    private fun updateData() {
        scope.launch {
            action.execute { profileRepository.updateData() }
            action.execute { profileRepository.updateBalance() }
        }
    }
}