package io.snaps.featureprofile.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basewallet.data.WalletRepository
import io.snaps.coredata.di.Bridged
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.featureprofile.presentation.toPhrases
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class BackupWalletKeyViewModel @Inject constructor(
    @Bridged walletRepository: WalletRepository,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState(walletRepository.getMnemonics().toPhrases()))
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    data class UiState(
        val phrase: List<Phrase>,
    )

    sealed class Command
}

data class Phrase(
    val orderNumber: Int,
    val text: String,
)