package io.snaps.featurewalletconnect.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basesession.data.SessionRepository
import io.snaps.coreui.viewmodel.SimpleViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class WalletConnectedViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onContinueButtonClicked() {
        _uiState.update { it.copy(isLoading = true) }
        sessionRepository.checkStatus()
    }

    data class UiState(
        val isLoading: Boolean = false,
    )

    sealed interface Command
}