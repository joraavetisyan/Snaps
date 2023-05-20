package io.snaps.basewallet.ui

import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.CoinValue
import io.snaps.corecommon.model.CryptoAddress
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.Token
import io.snaps.corecommon.model.TxHash
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
import kotlinx.serialization.Serializable
import javax.inject.Inject

@Serializable
data class TransferTokensSuccessData(
    val txHash: Token,
    val sent: CoinValue? = null,
    val to: CryptoAddress? = null,
    val type: Type? = null,
) {

    @Serializable
    enum class Type {
        Send, Sell
    }
}

interface TransferTokensDialogHandler {

    val transferTokensState: StateFlow<UiState>

    val transferTokensCommand: Flow<Command>

    fun showTransferTokensBottomDialog(scope: CoroutineScope, state: TransferTokensState)

    fun updateTransferTokensState(state: TransferTokensState)

    fun hideTransferTokensBottomDialog(scope: CoroutineScope)

    fun onSuccessfulTransfer(
        scope: CoroutineScope,
        data: TransferTokensSuccessData,
    )

    fun onSuccessfulSell(
        scope: CoroutineScope,
        data: TransferTokensSuccessData,
    )

    fun onTransferTokensDialogHidden()

    data class UiState(
        val state: TransferTokensState = TransferTokensState.Shimmer("".textValue()),
        val bottomDialog: BottomDialog? = null,
    )

    sealed class BottomDialog {
        object TokensTransfer : BottomDialog()
        data class TokensSellSuccess(
            val bscScanLink: FullUrl,
        ) : BottomDialog()
        data class TokensTransferSuccess(
            val bscScanLink: FullUrl,
            val sent: CoinValue?,
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

    override fun onSuccessfulTransfer(scope: CoroutineScope, data: TransferTokensSuccessData) {
        _uiState.update {
            it.copy(
                bottomDialog = TransferTokensDialogHandler.BottomDialog.TokensTransferSuccess(
                    bscScanLink = data.txHash.scanLink(),
                    sent = data.sent,
                    to = data.to,
                ),
            )
        }
        scope.launch {
            _command publish TransferTokensDialogHandler.Command.ShowBottomDialog
        }
    }

    // todo release mainnet scan
    private fun TxHash.scanLink() = "https://testnet.bscscan.com/tx/$this"

    override fun onSuccessfulSell(scope: CoroutineScope, data: TransferTokensSuccessData) {
        _uiState.update {
            it.copy(
                bottomDialog = TransferTokensDialogHandler.BottomDialog.TokensSellSuccess(
                    bscScanLink = data.txHash.scanLink(),
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