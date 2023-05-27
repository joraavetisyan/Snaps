package io.snaps.featurewallet.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basesources.NotificationsSource
import io.snaps.basewallet.data.WalletRepository
import io.snaps.basewallet.data.blockchain.BlockchainTxRepository
import io.snaps.basewallet.ui.LimitedGasDialogHandler
import io.snaps.basewallet.ui.TransferTokensDialogHandler
import io.snaps.basewallet.ui.TransferTokensState
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.applyDecimal
import io.snaps.corecommon.ext.stringAmountToDouble
import io.snaps.corecommon.ext.unapplyDecimal
import io.snaps.corecommon.model.CoinBNB
import io.snaps.corecommon.model.CoinValue
import io.snaps.corecommon.model.CryptoAddress
import io.snaps.basewallet.domain.WalletModel
import io.snaps.basewallet.ui.TransferTokensSuccessData
import io.snaps.corecommon.ext.stripTrailingZeros
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.coreuicompose.uikit.input.formatter.BigAmountFormatter
import io.snaps.coreuicompose.uikit.input.formatter.SimpleFormatter
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.math.BigInteger
import javax.inject.Inject

private const val gasLimitNull = -1L
private const val minGasValue = 0.001

@HiltViewModel
class WithdrawViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    transferTokensDialogHandler: TransferTokensDialogHandler,
    limitedGasDialogHandler: LimitedGasDialogHandler,
    private val action: Action,
    private val notificationsSource: NotificationsSource,
    @Bridged private val blockchainTxRepository: BlockchainTxRepository,
    @Bridged private val walletRepository: WalletRepository,
) : SimpleViewModel(),
    TransferTokensDialogHandler by transferTokensDialogHandler,
    LimitedGasDialogHandler by limitedGasDialogHandler {

    private val args = savedStateHandle.requireArgs<AppRoute.Withdraw.Args>()

    private val _uiState = MutableStateFlow(
        with(walletRepository.activeWallets.value.first { it.coinType == args.coin }) {
            UiState(
                walletModel = this,
                // todo subscribe on balance
                availableAmount = coinValue,
            )
        }
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private var gasLimitCalculateJob: Job? = null

    init {
        loadLegacyGasPrice()
    }

    private fun loadLegacyGasPrice() {
        viewModelScope.launch {
            action.execute {
                blockchainTxRepository.getLegacyGasPrice(_uiState.value.walletModel)
            }.doOnSuccess { gasPrice ->
                _uiState.update {
                    it.copy(
                        gasPrice = gasPrice,
                        gasValue = CoinBNB(gasPrice.unapplyDecimal(args.coin.decimal).toDouble()),
                    )
                }
            }
        }
    }

    private fun getValueDecimalApplied(): BigInteger? = _uiState.value.amountValue
        .applyDecimal(args.coin.decimal)
        ?.takeIf { it > BigInteger.ZERO }

    fun onAmountValueChanged(amount: String) {
        _uiState.update { it.copy(amountValue = amount) }
        scheduleGasLimitCalculate()
    }

    private fun scheduleGasLimitCalculate() {
        gasLimitCalculateJob?.cancel()
        val value = getValueDecimalApplied()
        if (value == null) {
            _uiState.update { it.copy(isCalculating = false, isAddressInvalid = false, gasLimit = gasLimitNull) }
            return
        }
        val address = with(_uiState.value.addressValue) {
            when {
                walletRepository.isAddressValid(this) -> this
                else -> null
            }
        }
        if (address == null) {
            _uiState.update { it.copy(isCalculating = false, isAddressInvalid = true, gasLimit = gasLimitNull) }
            return
        }
        _uiState.update { it.copy(isCalculating = true, isAddressInvalid = false, gasLimit = gasLimitNull) }
        gasLimitCalculateJob = viewModelScope.launch {
            delay(300L)
            if (!isActive) return@launch
            action.execute {
                blockchainTxRepository.calculateGasLimit(
                    wallet = _uiState.value.walletModel,
                    address = address,
                    value = value,
                    gasPrice = _uiState.value.gasPrice,
                )
            }.doOnSuccess { gasLimit ->
                if (!isActive) return@doOnSuccess
                _uiState.update { it.copy(gasLimit = gasLimit) }
            }.doOnComplete {
                _uiState.update { it.copy(isCalculating = false) }
            }
        }
    }

    fun onAddressValueChanged(address: String) {
        _uiState.update { it.copy(addressValue = address) }
        scheduleGasLimitCalculate()
    }

    fun onMaxButtonClicked() {
        _uiState.update { state ->
            state.copy(amountValue = state.availableAmount?.value?.stripTrailingZeros().orEmpty())
        }
        scheduleGasLimitCalculate()
    }

    fun onConfirmTransactionClicked() {
        val wallet = _uiState.value.walletModel
        showTransferTokensBottomDialog(
            scope = viewModelScope,
            state = TransferTokensState.Data(
                title = StringKey.WithdrawDialogWithdrawTitle.textValue(wallet.coinType.symbol),
                from = wallet.receiveAddress,
                to = _uiState.value.addressValue,
                summary = CoinValue(wallet.coinType, _uiState.value.amountValue.stringAmountToDouble()),
                gas = _uiState.value.gasValue,
                // todo correct sum of summary + gas
                total = CoinValue(wallet.coinType, _uiState.value.amountValue.stringAmountToDouble()),
                onConfirmClick = ::onSendTransactionClicked,
                onCancelClick = { hideTransferTokensBottomDialog(scope = viewModelScope) },
            ),
        )
    }

    private fun onSendTransactionClicked() {
        hideTransferTokensBottomDialog(scope = viewModelScope)
        onTransferTokensDialogHidden()
        val amount = getValueDecimalApplied() ?: kotlin.run {
            viewModelScope.launch { notificationsSource.sendError(StringKey.WithdrawErrorInvalidAmount.textValue()) }
            return
        }

        checkGas(
            scope = viewModelScope,
            minValue = minGasValue,
            onGasEnough = {
                _uiState.update { it.copy(isLoading = true) }
                action.execute {
                    blockchainTxRepository.send(
                        wallet = _uiState.value.walletModel,
                        address = _uiState.value.addressValue,
                        amount = amount,
                        gasPrice = _uiState.value.gasPrice,
                        gasLimit = _uiState.value.gasLimit,
                    )
                }.doOnComplete {
                    _uiState.update { it.copy(isLoading = false) }
                }.doOnSuccess {
                    _command publish Command.CloseScreenOnSuccess(
                        TransferTokensSuccessData(
                            txHash = it,
                            sent = CoinValue(
                                _uiState.value.walletModel.coinType,
                                _uiState.value.amountValue.stringAmountToDouble(),
                            ),
                            to = _uiState.value.addressValue,
                            type = TransferTokensSuccessData.Type.Send,
                        )
                    )
                }
            },
            onSync = { notificationsSource.sendError(StringKey.ErrorBalanceInSync.textValue()) },
        )
    }

    data class UiState(
        val walletModel: WalletModel,
        val isLoading: Boolean = false,
        val isCalculating: Boolean = false,
        val isAddressInvalid: Boolean = false,
        val addressValue: CryptoAddress = "",
        val amountValue: String = "",
        val availableAmount: CoinValue? = null,
        val gasValue: CoinValue = CoinBNB(0.0),

        val amountFormatter: SimpleFormatter = BigAmountFormatter(),

        val gasPrice: Long? = null,
        val gasLimit: Long = gasLimitNull,
    ) {

        val isConfirmEnabled: Boolean
            get() = !isAddressInvalid
                && gasLimit != gasLimitNull
                && addressValue.isNotBlank()
                && amountValue.isNotBlank()
    }

    sealed interface Command {
        data class CloseScreenOnSuccess(val data: TransferTokensSuccessData) : Command
    }
}