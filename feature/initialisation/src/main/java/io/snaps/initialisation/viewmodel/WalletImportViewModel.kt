package io.snaps.initialisation.viewmodel

import io.snaps.coreui.viewmodel.SimpleViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class WalletImportViewModel @Inject constructor() : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onContinueButtonClicked() { /*TODO*/ }

    fun onPhraseValueChanged(phrase: String, index: Int) {
        _uiState.update {
            val newPhrases = it.phrases.mapIndexed { i, s ->
                if (index == i) phrase else s
            }
            it.copy(phrases = newPhrases)
        }
    }

    data class UiState(
        val phrases: List<String> = List(12) { "" },
    ) {

        val isContinueButtonEnabled get() = phrases.forEach { phrase ->
            phrase.isNotBlank()
        }
    }

    sealed class Command
}