package io.snaps.featurewalletconnect.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basewallet.data.WalletRepository
import io.snaps.coredata.di.Bridged
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
class MnemonicsViewModel @Inject constructor(
    @Bridged walletRepository: WalletRepository,
    @Bridged profileRepository: ProfileRepository,
    action: Action,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        viewModelScope.launch {
            action.execute {
                profileRepository.updateData()
            }.doOnSuccess { user ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        words = walletRepository.createAccount(user.userId),
                    )
                }
            }.doOnComplete {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onContinueButtonClicked() {
        viewModelScope.launch {
            _command publish Command.OpenVerificationScreen(_uiState.value.words)
        }
    }

    data class UiState(
        val isLoading: Boolean = true,
        val words: List<String> = emptyList(),
    ) {

        val isContinueButtonEnabled: Boolean
            get() = words.isNotEmpty()
    }

    sealed interface Command {

        data class OpenVerificationScreen(val words: List<String>) : Command
    }
}