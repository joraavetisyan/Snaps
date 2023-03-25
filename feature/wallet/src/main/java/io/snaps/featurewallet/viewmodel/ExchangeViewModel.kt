package io.snaps.featurewallet.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.corecommon.model.WalletModel
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.viewmodel.SimpleViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    cryptoSendHandlerDelegate: CryptoSendHandler,
) : SimpleViewModel(),
    CryptoSendHandler by cryptoSendHandlerDelegate {

    private val args = savedStateHandle.requireArgs<AppRoute.Exchange.Args>()

    private val _uiState = MutableStateFlow(UiState(args.wallet))
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
    }

    fun onTransactionSendClicked(
        address: String,
        amount: String,
    ) {
        onSendClicked(viewModelScope, args.wallet, address, amount)
    }

    data class UiState(
        val walletModel: WalletModel,
    )

    sealed class Command
}