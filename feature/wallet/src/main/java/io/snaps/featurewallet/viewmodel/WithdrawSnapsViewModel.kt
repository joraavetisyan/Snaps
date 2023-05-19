package io.snaps.featurewallet.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.data.model.PaymentsState
import io.snaps.basewallet.data.WalletRepository
import io.snaps.basewallet.data.blockchain.BlockchainTxRepository
import io.snaps.basewallet.ui.LimitedGasDialogHandler
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.ext.toStringValue
import io.snaps.corecommon.model.WalletModel
import io.snaps.corecommon.strings.digitsOnly
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreuicompose.uikit.input.formatter.AmountFormatter
import io.snaps.coreuicompose.uikit.input.formatter.CardNumberFormatter
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
import java.math.BigInteger
import javax.inject.Inject

private const val gasLimitNull = -1L
private const val gasLimitCalcAddress = "0x173bc87d7D4bAa9cAB1166E5B0E714aC4ad10566"
private const val minGasValue = 0.001

@HiltViewModel
class WithdrawSnapsViewModel @Inject constructor(
    limitedGasDialogHandler: LimitedGasDialogHandler,
    private val action: Action,
    @Bridged private val profileRepository: ProfileRepository,
    @Bridged private val walletRepository: WalletRepository,
    @Bridged private val blockchainTxRepository: BlockchainTxRepository,
) : SimpleViewModel(),
    LimitedGasDialogHandler by limitedGasDialogHandler {

    private val snpWallet = walletRepository.getSnpWalletModel()

    private val _uiState = MutableStateFlow(
        UiState(availableAmount = snpWallet?.coinValueDouble?.toStringValue() ?: "-")
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private var gasLimitCalculateJob: Job? = null

    init {
        loadLegacyGasPrice()
    }

    private fun loadLegacyGasPrice() {
        val snpWalletModel = snpWallet ?: kotlin.run { log("No SNAPS wallet!"); return }
        viewModelScope.launch {
            action.execute {
                blockchainTxRepository.getLegacyGasPrice(snpWalletModel)
            }.doOnSuccess { gasPrice -> _uiState.update { it.copy(gasPrice = gasPrice) } }
        }
    }

    fun onMaxButtonClicked() {
        _uiState.update { state ->
            state.copy(amountValue = state.availableAmount.filter { it.isDigit() || it == '.' })
        }
    }

    fun onAmountValueChanged(value: String) {
        _uiState.update {
            it.copy(
                amountValue = value,
                total = profileRepository.balanceState.value.dataOrCache
                    ?.snpExchangeRate
                    ?.let { rate ->
                        value.replace(',', '.').toDoubleOrNull()?.times(rate)
                    }
                    ?.toStringValue()
                    .orEmpty(),
            )
        }
        scheduleGasLimitCalculate()
    }

    private fun scheduleGasLimitCalculate() {
        val snpWalletModel = snpWallet ?: kotlin.run { log("No SNAPS wallet!"); return }
        gasLimitCalculateJob?.cancel()
        val value = getAmount()
        if (value == null) {
            _uiState.update { it.copy(isCalculating = false, gasLimit = gasLimitNull) }
            return
        }
        _uiState.update { it.copy(isCalculating = true, gasLimit = gasLimitNull) }
        gasLimitCalculateJob = viewModelScope.launch {
            delay(300L)
            if (!isActive) return@launch
            action.execute {
                blockchainTxRepository.calculateGasLimit(
                    wallet = snpWalletModel,
                    address = gasLimitCalcAddress,
                    value = value,
                    gasPrice = _uiState.value.gasPrice,
                )
            }.doOnSuccess { gasLimit ->
                if (!isActive) return@doOnSuccess
                log("gas limit $gasLimit")
                _uiState.update { it.copy(gasLimit = gasLimit) }
            }.doOnComplete {
                _uiState.update { it.copy(isCalculating = false) }
            }
        }
    }

    private fun getAmount() = getAmountCorrected()
        .toBigDecimalOrNull()
        ?.movePointRight(18)
        ?.takeIf { it > BigDecimal.ZERO }
        ?.toBigInteger()

    private fun getAmountCorrected() = _uiState.value.amountValue.replace(',', '.')

    fun onCardNumberValueChanged(value: String) {
        _uiState.update { it.copy(cardNumberValue = value) }
    }

    fun onRepeatCardNumberValueChanged(value: String) {
        _uiState.update { it.copy(repeatCardNumberValue = value) }
    }

    fun onSendClicked() {
        val amount = getAmount() ?: kotlin.run { log("Invalid amount!"); return }
        val snpWalletModel = snpWallet ?: kotlin.run { log("No SNAPS wallet!"); return }
        when (profileRepository.state.value.dataOrCache?.paymentsState) {
            null,
            PaymentsState.No,
            PaymentsState.InApp -> checkGas(viewModelScope, minGasValue) {
                send(snpWalletModel = snpWalletModel, amount = amount)
            }
            PaymentsState.Blockchain -> viewModelScope.launch {
                send(snpWalletModel = snpWalletModel, amount = amount)
            }
        }
    }

    private suspend fun send(snpWalletModel: WalletModel, amount: BigInteger) {
        _uiState.update { it.copy(isLoading = true) }
        action.execute {
            blockchainTxRepository.getProfitWalletAddress().flatMap {
                blockchainTxRepository.send(
                    wallet = snpWalletModel,
                    address = it,
                    amount = amount,
                    gasPrice = _uiState.value.gasPrice,
                    gasLimit = _uiState.value.gasLimit,
                )
            }.flatMap {
                walletRepository.confirmPayout(
                    amount = getAmountCorrected().toDouble(),
                    cardNumber = _uiState.value.cardNumberValue.digitsOnly(),
                )
            }
        }.doOnSuccess {

        }.doOnComplete {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val isCalculating: Boolean = false,
        val availableAmount: String,
        val amountValue: String = "",
        val amountFormatter: SimpleFormatter = AmountFormatter(
            maxLength = 100,
            fractionalPartMaxLength = 100,
        ),
        val cardNumberValue: String = "",
        val cardNumberFormatter: SimpleFormatter = CardNumberFormatter("#### #### #### ####"),
        val repeatCardNumberValue: String = "",
        val gasPrice: Long? = null,
        val gasLimit: Long = gasLimitNull,
        val commission: String = "2",
        val total: String = "0",
    ) {

        val isSendEnabled: Boolean
            get() = !isCalculating
                && gasLimit != gasLimitNull
                && amountValue.isNotBlank()
                && cardNumberValue.digitsOnly().length == 16
                && repeatCardNumberValue.digitsOnly().length == 16
                && cardNumberValue.digitsOnly() == repeatCardNumberValue.digitsOnly()
    }

    sealed interface Command {
        object CloseScreen : Command
    }
}