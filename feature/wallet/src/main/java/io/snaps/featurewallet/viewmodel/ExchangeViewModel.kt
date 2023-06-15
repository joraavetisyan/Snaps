package io.snaps.featurewallet.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basesources.NotificationsSource
import io.snaps.basewallet.data.blockchain.BlockchainTxRepository
import io.snaps.basewallet.data.WalletRepository
import io.snaps.basewallet.domain.SwapTransactionModel
import io.snaps.corecommon.container.textValue
import io.snaps.basewallet.domain.WalletModel
import io.snaps.basewallet.ui.TransferTokensDialogHandler
import io.snaps.basewallet.ui.TransferTokensSuccessData
import io.snaps.corecommon.ext.unapplyDecimal
import io.snaps.corecommon.model.CoinBNB
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.viewmodel.SimpleViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    transferTokensDialogHandler: TransferTokensDialogHandler,
    private val action: Action,
    private val notificationsSource: NotificationsSource,
    @Bridged private val walletRepository: WalletRepository,
    @Bridged private val blockchainTxRepository: BlockchainTxRepository,
) : SimpleViewModel(),
    TransferTokensDialogHandler by transferTokensDialogHandler {

    private val args = savedStateHandle.requireArgs<AppRoute.Exchange.Args>()

    private val _uiState = MutableStateFlow(
        UiState(walletModel = walletRepository.activeWallets.value.first { it.coinType == args.coin })
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onTransactionSendClicked(transactionModel: SwapTransactionModel) {
        walletRepository.bnb.value?.let {
            _uiState.update { it.copy(isLoading = true) }
            viewModelScope.launch {
                action.execute {
                    blockchainTxRepository.send(
                        wallet = it,
                        address = transactionModel.address,
                        amount = transactionModel.amount,
                        gasPrice = transactionModel.gasPrice.toLong(),
                        gasLimit = transactionModel.gasLimit.toLong(),
                        data = transactionModel.data,
                    )
                }.doOnComplete {
                    _uiState.update { it.copy(isLoading = false) }
                }.doOnSuccess {
                    notificationsSource.sendMessage(StringKey.MessageSuccess.textValue())
                    onSuccessfulTransfer(
                        scope = viewModelScope,
                        data = TransferTokensSuccessData(
                            txHash = it,
                            to = transactionModel.address,
                            sent = CoinBNB(transactionModel.amount.toLong().unapplyDecimal(args.coin.decimal).toDouble()),
                        ),
                    )
                }
            }
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val walletModel: WalletModel,
    )

    sealed interface Command {
        object CloseScreen : Command
    }
}