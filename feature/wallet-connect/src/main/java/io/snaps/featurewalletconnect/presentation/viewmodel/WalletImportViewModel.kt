package io.snaps.featurewalletconnect.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesession.data.SessionRepository
import io.snaps.basewallet.data.WalletRepository
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletImportViewModel @Inject constructor(
    walletSecurityErrorHandler: WalletSecurityErrorHandler,
    @Bridged private val walletRepository: WalletRepository,
    @Bridged private val profileRepository: ProfileRepository,
    private val sessionRepository: SessionRepository,
    private val action: Action,
) : SimpleViewModel(), WalletSecurityErrorHandler by walletSecurityErrorHandler {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onContinueButtonClicked() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            action.execute {
                // todo to interactor
                profileRepository.updateData()
                    .flatMap { user ->
                        walletRepository.importAccount(
                            userId = user.userId,
                            words = _uiState.value.words.trim().lowercase().split(" "),
                        )
                    }.flatMap {
                        sessionRepository.onWalletConnected()
                    }
            }.doOnError { error, _ ->
                handleWalletSecurityError(error)
            }.doOnComplete {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onPhraseValueChanged(phrase: String) {
        _uiState.update { it.copy(words = phrase) }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val words: String = "",
    ) {

        val isContinueEnabled get() = words.isNotBlank()
    }

    sealed interface Command
}