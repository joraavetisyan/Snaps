package io.snaps.basewallet.ui

import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.Token
import io.snaps.corecommon.model.CryptoAddress
import io.snaps.coreui.viewmodel.publish
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

interface TransferTokensDialogHandler {

    val transferTokensState: StateFlow<UiState>

    val transferTokensCommand: Flow<Command>

    fun showTransferTokensBottomDialog(scope: CoroutineScope, state: TransferTokensState)

    fun updateTransferTokensState(state: TransferTokensState)

    fun hideTransferTokensBottomDialog(scope: CoroutineScope)

    fun onSuccessfulTransfer(
        scope: CoroutineScope,
        txHash: Token,
        sent: String? = null,
        to: CryptoAddress? = null,
    )

    fun onTransferTokensDialogHidden()

    data class UiState(
        val state: TransferTokensState = TransferTokensState.Shimmer("".textValue()),
        val bottomDialog: BottomDialog? = null,
    )

    sealed class BottomDialog {
        object TokensTransfer : BottomDialog()
        data class TokensTransferSuccess(
            val bscScanLink: FullUrl,
            val sent: String?,
            val to: CryptoAddress?,
        ) : BottomDialog()
    }

    sealed class Command {
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
    }
}

class TransferTokensDialogHandlerImplDelegate @Inject constructor() : TransferTokensDialogHandler {

    private val _uiState = MutableStateFlow(TransferTokensDialogHandler.UiState())
    override val transferTokensState = _uiState.asStateFlow()

    private val _command = Channel<TransferTokensDialogHandler.Command>()
    override val transferTokensCommand = _command.receiveAsFlow()

    override fun updateTransferTokensState(state: TransferTokensState) {
        _uiState.update { it.copy(state = state) }
    }

    override fun showTransferTokensBottomDialog(scope: CoroutineScope, state: TransferTokensState) {
        _uiState.update {
            it.copy(
                bottomDialog = TransferTokensDialogHandler.BottomDialog.TokensTransfer,
                state = state,
            )
        }
        scope.launch { _command publish TransferTokensDialogHandler.Command.ShowBottomDialog }
    }

    override fun hideTransferTokensBottomDialog(scope: CoroutineScope) {
        scope.launch { _command publish TransferTokensDialogHandler.Command.HideBottomDialog }
    }

    override fun onSuccessfulTransfer(scope: CoroutineScope, txHash: Token, sent: String?, to: CryptoAddress?) {
        _uiState.update {
            it.copy(
                bottomDialog = TransferTokensDialogHandler.BottomDialog.TokensTransferSuccess(
                    // todo release mainnet scan
                    bscScanLink = "https://testnet.bscscan.com/tx/${txHash}", sent = sent, to = to
                ),
            )
        }
        scope.launch {
            _command publish TransferTokensDialogHandler.Command.ShowBottomDialog
        }
    }

    override fun onTransferTokensDialogHidden() {
        _uiState.update { it.copy(bottomDialog = null) }
    }
}