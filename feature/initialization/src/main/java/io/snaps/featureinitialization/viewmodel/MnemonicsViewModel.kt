package io.snaps.featureinitialization.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.featureinitialization.data.WalletRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MnemonicsViewModel @Inject constructor(
    walletRepository: WalletRepository,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState(words = walletRepository.createAccount()))
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onContinueButtonClicked() {
        viewModelScope.launch {
            _command publish Command.OpenVerificationScreen(_uiState.value.words)
        }
    }

    data class UiState(
        val words: List<String> = emptyList(),
    )

    sealed interface Command {

        data class OpenVerificationScreen(val words: List<String>) : Command
    }
}