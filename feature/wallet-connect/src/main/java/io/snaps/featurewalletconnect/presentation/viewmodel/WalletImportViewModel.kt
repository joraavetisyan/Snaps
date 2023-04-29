package io.snaps.featurewalletconnect.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesession.data.SessionRepository
import io.snaps.basewallet.data.WalletRepository
import io.snaps.basewallet.domain.DeviceNotSecuredException
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
    private val walletRepository: WalletRepository,
    private val profileRepository: ProfileRepository,
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
                // todo to interactor
                profileRepository.updateData()
                    .flatMap { user ->
                        walletRepository.importAccount(
                            userId = user.userId,
                            words = _uiState.value.words.first().let {
                                it.trim().split(" ").map { it.lowercase() }
                            }
                        )
                    }.flatMap {
                        sessionRepository.onWalletConnect()
                    }
            }.doOnError { error, _ ->
                if (error.cause is DeviceNotSecuredException) {
                    _uiState.update { it.copy(dialog = Dialog.DeviceNotSecured, isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun onDialogDismissRequested() {
        _uiState.update { it.copy(dialog = null) }
    }

    fun onPhraseValueChanged(phrase: String, index: Int) {
        _uiState.update {
            val newPhrases = it.words.mapIndexed { i, s -> if (index == i) phrase else s }
            it.copy(words = newPhrases)
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val words: List<String> = List(12) { "" },
        val dialog: Dialog? = null,
    ) {

        val isContinueButtonEnabled get() = true // todo
    }

    sealed interface Command

    enum class Dialog {
        DeviceNotSecured,
    }
}