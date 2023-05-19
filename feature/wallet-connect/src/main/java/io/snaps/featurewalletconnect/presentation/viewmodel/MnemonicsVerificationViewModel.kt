package io.snaps.featurewalletconnect.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basewallet.data.WalletRepository
import io.snaps.basewallet.domain.DeviceNotSecuredException
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.featurewalletconnect.presentation.screen.SelectorTileStatus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MnemonicsVerificationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @Bridged private val walletRepository: WalletRepository,
    private val action: Action,
) : SimpleViewModel() {

    private val args = savedStateHandle.requireArgs<AppRoute.MnemonicsVerification.Args>()

    private val _uiState = MutableStateFlow(
        UiState(
            words = args.words.mapIndexed { index, s ->
                WordUiModel(
                    text = s, ordinal = index + 1, status = SelectorTileStatus.Default
                )
            }
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        randomize()
    }

    private fun randomize() {
        val ordinals = (1..args.words.size).toMutableList()
        val randomOrdinals = mutableListOf<Int>()
        repeat(4) {
            val ordinal = ordinals.removeAt((0 until ordinals.size).random())
            randomOrdinals.add(ordinal)
        }
        randomOrdinals.sort()
        val wordPool = _uiState.value.words.mapNotNull {
            it.takeUnless { it.ordinal in randomOrdinals }
        }.shuffled()
        var sublistStart = 0
        val selections = randomOrdinals.map { ordinal ->
            SelectionUiModel(
                ordinal = ordinal,
                words = (wordPool.subList(sublistStart, sublistStart + 2).also {
                    sublistStart += 2
                } + _uiState.value.words.first { it.ordinal == ordinal }).shuffled(),
            )
        }
        _uiState.update {
            it.copy(selections = selections)
        }
    }

    fun onContinueButtonClicked() = viewModelScope.launch {
        action.execute {
            walletRepository.saveLastConnectedAccount()
        }.doOnSuccess {
            _command publish Command.OpenCreatedWalletScreen
        }.doOnError { error, _ ->
            if (error.cause is DeviceNotSecuredException) {
                _uiState.update { it.copy(dialog = Dialog.DeviceNotSecured) }
            }
        }
    }

    fun onDialogDismissRequested() {
        _uiState.update { it.copy(dialog = null) }
    }

    fun onWordItemClicked(selection: SelectionUiModel, word: WordUiModel) {
        _uiState.update { state ->
            state.copy(
                selections = state.selections.map { item ->
                    if (item.ordinal == selection.ordinal && !item.isSelected()) {
                        item.copy(
                            words = item.words.map {
                                if (it.text == word.text) {
                                    it.copy(
                                        status = if (item.isCorrect(word)) {
                                            SelectorTileStatus.Selected
                                        } else {
                                            SelectorTileStatus.Error
                                        }
                                    )
                                } else it
                            }
                        )
                    } else item
                }
            )
        }
    }

    fun onAnimationFinished(selection: SelectionUiModel, word: WordUiModel) {
        _uiState.update { state ->
            val newPhrases = state.selections.map { item ->
                if (item.ordinal == selection.ordinal) {
                    item.copy(
                        words = item.words.map {
                            if (it != word) it
                            else it.copy(status = SelectorTileStatus.Default)
                        }
                    )
                } else item
            }
            state.copy(selections = newPhrases)
        }
    }

    data class UiState(
        val words: List<WordUiModel> = emptyList(),
        val selections: List<SelectionUiModel> = emptyList(),
        val dialog: Dialog? = null,
    ) {

        val isContinueButtonEnabled
            get() = selections.all(SelectionUiModel::isSelected)
    }

    sealed class Command {
        object OpenCreatedWalletScreen : Command()
    }

    enum class Dialog {
        DeviceNotSecured,
    }
}

data class SelectionUiModel(
    val ordinal: Int,
    val words: List<WordUiModel>,
) {

    fun isCorrect(word: WordUiModel) = word.ordinal == ordinal

    fun isSelected() = words.first(::isCorrect).status == SelectorTileStatus.Selected
}

data class WordUiModel(
    val ordinal: Int,
    val text: String,
    val status: SelectorTileStatus,
)