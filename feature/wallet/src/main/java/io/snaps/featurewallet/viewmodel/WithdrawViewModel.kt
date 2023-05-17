package io.snaps.featurewallet.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basesources.NotificationsSource
import io.snaps.basewallet.data.blockchain.BlockchainTxRepository
import io.snaps.basewallet.data.WalletRepository
import io.snaps.basewallet.ui.TransferTokensDialogHandler
import io.snaps.basewallet.ui.TransferTokensState
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.toStringValue
import io.snaps.corecommon.model.FiatCurrency
import io.snaps.corecommon.model.WalletAddress
import io.snaps.corecommon.model.WalletModel
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreuicompose.uikit.input.formatter.AmountFormatter
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
import java.math.BigDecimal
import javax.inject.Inject

private const val gasLimitNull = -1L

@HiltViewModel
class WithdrawViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    transferTokensDialogHandlerImplDelegate: TransferTokensDialogHandler,
    private val action: Action,
    private val notificationsSource: NotificationsSource,
    private val blockchainTxRepository: BlockchainTxRepository,
    private val walletRepository: WalletRepository,
) : SimpleViewModel(), TransferTokensDialogHandler by transferTokensDialogHandlerImplDelegate {

    private val args = savedStateHandle.requireArgs<AppRoute.Withdraw.Args>()

    private val _uiState = MutableStateFlow(
        UiState(
            walletModel = args.wallet,
            // todo subscribe on balance
            availableAmount = "${args.wallet.coinValueDouble.toStringValue()} ${args.wallet.symbol}",
        )
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
                blockchainTxRepository.getLegacyGasPrice(args.wallet)
            }.doOnSuccess { gasPrice ->
                _uiState.update {
                    it.copy(
                        gasPrice = gasPrice,
                        gasPriceString = "${gasPrice.toStringValue()} BNB",
                    )
                }
            }
        }
    }

    private fun Long.toStringValue() = moveLeft().toStringValue()

    private fun Long.moveLeft(): BigDecimal = toBigDecimal().movePointLeft(args.wallet.decimal)

    fun onAmountValueChanged(amount: String) {
        _uiState.update { it.copy(amountValue = amount) }
        scheduleGasLimitCalculate()
    }

    private fun scheduleGasLimitCalculate() {
        gasLimitCalculateJob?.cancel()
        val value = getAmount()
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
                    wallet = args.wallet, address = address, value = value, gasPrice = _uiState.value.gasPrice
                )
            }.doOnSuccess { gasLimit ->
                if (!isActive) return@doOnSuccess
                _uiState.update { it.copy(gasLimit = gasLimit) }
            }.doOnComplete {
                _uiState.update { it.copy(isCalculating = false) }
            }
        }
    }

    private fun getAmount() = _uiState.value.amountValue
        .replace(',', '.')
        .toBigDecimalOrNull()
        ?.movePointRight(args.wallet.decimal)
        ?.takeIf { it > BigDecimal.ZERO }
        ?.toBigInteger()

    fun onAddressValueChanged(address: String) {
        _uiState.update { it.copy(addressValue = address) }
        scheduleGasLimitCalculate()
    }

    fun onMaxButtonClicked() {
        _uiState.update { state ->
            state.copy(amountValue = state.availableAmount.filter { it.isDigit() || it == '.' })
        }
        scheduleGasLimitCalculate()
    }

    fun onConfirmTransactionClicked() {
        showTransferTokensBottomDialog(
            scope = viewModelScope,
            state = TransferTokensState.Data(
                title = "Withdraw ${args.wallet.symbol}".textValue(),
                from = args.wallet.receiveAddress,
                to = _uiState.value.addressValue,
                summary = "${_uiState.value.amountValue} ${args.wallet.symbol}",
                gas = _uiState.value.gasPriceString,
                total = "${_uiState.value.amountValue} ${args.wallet.symbol}",
                onConfirmClick = ::onSendTransactionClicked,
                onCancelClick = { hideTransferTokensBottomDialog(scope = viewModelScope) },
            ),
        )
    }

    private fun onSendTransactionClicked() {
        hideTransferTokensBottomDialog(scope = viewModelScope)
        val amount = getAmount() ?: kotlin.run {
            viewModelScope.launch { notificationsSource.sendError("Invalid amount".textValue()) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            action.execute {
                blockchainTxRepository.send(
                    wallet = args.wallet,
                    walletAddress = _uiState.value.addressValue,
                    amount = amount,
                    gasPrice = _uiState.value.gasPrice,
                    gasLimit = _uiState.value.gasLimit,
                )
            }.doOnComplete {
                _uiState.update { it.copy(isLoading = false) }
            }.doOnSuccess {
                onSuccessfulTransfer(
                    scope = viewModelScope,
                    txHash = it,
                    sent = "${_uiState.value.amountValue} ${args.wallet.symbol}",
                    to = _uiState.value.addressValue,
                )
            }
        }
    }

    data class UiState(
        val walletModel: WalletModel,
        val isLoading: Boolean = false,
        val isAddressInvalid: Boolean = false,
        val isCalculating: Boolean = false,
        val addressValue: WalletAddress = "",
        val amountValue: String = "",
        val availableAmount: String = "",
        val gasPrice: Long? = null,
        val gasLimit: Long = gasLimitNull,
        val gasPriceString: String = "",
        val amountFormatter: SimpleFormatter = AmountFormatter(
            fiatCurrency = FiatCurrency.NONE,
            maxLength = 100,
            fractionalPartMaxLength = 100,
        ),
    ) {

        val isConfirmEnabled: Boolean
            get() = !isAddressInvalid
                && gasLimit != gasLimitNull
                && addressValue.isNotBlank()
                && amountValue.isNotBlank()
    }

    sealed interface Command {
        object CloseScreen : Command
    }
}