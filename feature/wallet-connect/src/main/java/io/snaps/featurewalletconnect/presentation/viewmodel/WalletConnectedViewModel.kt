package io.snaps.featurewalletconnect.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basesession.data.SessionRepository
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
class WalletConnectedViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val action: Action,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onContinueButtonClicked() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            action.execute {
                sessionRepository.checkStatus()
            }.doOnComplete {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
    )

    sealed interface Command
}