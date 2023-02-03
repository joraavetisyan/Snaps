package io.snaps.featureregistration.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.featureregistration.presentation.screen.SelectorTileStatus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor() : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onContinueButtonClicked() = viewModelScope.launch {
        _command publish Command.OpenCreatedWalletScreen
    }

    fun onPhraseItemClicked(phrase: Phrase) {
        _uiState.update {
            val newPhrases = it.shuffledPhrases.mapIndexed { index, item ->
                if (phrase.orderNumber == item.orderNumber) {
                    if (phrase.orderNumber != index + 1) {
                        phrase.copy(status = SelectorTileStatus.Error)
                    } else if (phrase.status == SelectorTileStatus.Default) {
                        phrase.copy(status = SelectorTileStatus.Selected)
                    } else {
                        item
                    }
                } else item
            }
            it.copy(shuffledPhrases = newPhrases)
        }
    }

    fun onAnimationFinished(phrase: Phrase) {
        _uiState.update {
            val newPhrases = it.shuffledPhrases.map { item ->
                if (phrase.orderNumber == item.orderNumber) {
                    phrase.copy(status = SelectorTileStatus.Default)
                } else item
            }
            it.copy(shuffledPhrases = newPhrases)
        }
    }

    data class UiState(
        val phrases: List<Phrase> = listOf(
            Phrase("load", 1, SelectorTileStatus.Default),
            Phrase("good", 2, SelectorTileStatus.Default),
            Phrase("mamba", 3, SelectorTileStatus.Default),
            Phrase("sweet", 4, SelectorTileStatus.Default),
            Phrase("come", 5, SelectorTileStatus.Default),
            Phrase("ligekscg", 6, SelectorTileStatus.Default),
            Phrase("moloko", 7, SelectorTileStatus.Default),
            Phrase("sooq", 8, SelectorTileStatus.Default),
            Phrase("eghtins", 9, SelectorTileStatus.Default),
            Phrase("weluy", 10, SelectorTileStatus.Default),
            Phrase("game", 11, SelectorTileStatus.Default),
            Phrase("opeda", 12, SelectorTileStatus.Default),
        ),
        val shuffledPhrases: List<Phrase> = phrases.shuffled(),
    ) {

        val isContinueButtonEnabled
            get() = shuffledPhrases.all { phrase ->
                phrase.status == SelectorTileStatus.Selected
            }
    }

    sealed class Command {
        object OpenCreatedWalletScreen : Command()
    }
}

data class Phrase(
    val text: String,
    val orderNumber: Int,
    val status: SelectorTileStatus,
)