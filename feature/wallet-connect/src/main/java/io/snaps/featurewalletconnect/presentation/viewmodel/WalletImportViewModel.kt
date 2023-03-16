package io.snaps.featurewalletconnect.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basewallet.data.WalletRepository
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletImportViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val action: Action,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onContinueButtonClicked() {
        viewModelScope.launch {
            action.execute {
                walletRepository.importAccount(_uiState.value.words)
            }.doOnSuccess {
                _command publish Command.OpenCreateUserScreen
            }.doOnError { _, _ ->
                _uiState.update { it.copy(hasError = true) }
            }
        }
    }

    fun onPhraseValueChanged(phrase: String, index: Int) {
        _uiState.update {
            val newPhrases = it.words.mapIndexed { i, s -> if (index == i) phrase else s }
            it.copy(hasError = false, words = newPhrases)
        }
    }

    data class UiState(
        val hasError: Boolean = false,
        val words: List<String> = List(12) { "" },
    ) {

        val isContinueButtonEnabled get() = !hasError && words.all(String::isNotBlank)
    }

    sealed interface Command {

        object OpenCreateUserScreen : Command
    }
}