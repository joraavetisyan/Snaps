package io.snaps.featurewallet.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.data.model.PaymentsState
import io.snaps.basewallet.data.WalletRepository
import io.snaps.basewallet.data.blockchain.BlockchainTxRepository
import io.snaps.basewallet.domain.WalletModel
import io.snaps.basewallet.ui.LimitedGasDialogHandler
import io.snaps.basewallet.ui.TransferTokensDialogHandler
import io.snaps.basewallet.ui.TransferTokensSuccessData
import io.snaps.corecommon.ext.applyDecimal
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.ext.stringAmountToDouble
import io.snaps.corecommon.ext.stringAmountToDoubleOrZero
import io.snaps.corecommon.ext.stringAmountToDoubleSafely
import io.snaps.corecommon.ext.stripTrailingZeros
import io.snaps.corecommon.model.CoinSNPS
import io.snaps.corecommon.model.CoinType
import io.snaps.corecommon.model.FiatUSD
import io.snaps.corecommon.model.FiatValue
import io.snaps.corecommon.strings.digitsOnly
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.coreuicompose.uikit.input.formatter.BigAmountFormatter
import io.snaps.coreuicompose.uikit.input.formatter.CardNumberFormatter
import io.snaps.coreuicompose.uikit.input.formatter.SimpleFormatter
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.math.BigInteger
import javax.inject.Inject

private const val gasLimitNull = -1L
private const val gasLimitCalcAddress = "0x173bc87d7D4bAa9cAB1166E5B0E714aC4ad10566"
private const val minGasValue = 0.001

@HiltViewModel
class WithdrawSnapsViewModel @Inject constructor(
    limitedGasDialogHandler: LimitedGasDialogHandler,
    transferTokensDialogHandler: TransferTokensDialogHandler,
    private val action: Action,
    @Bridged private val profileRepository: ProfileRepository,
    @Bridged private val walletRepository: WalletRepository,
    @Bridged private val blockchainTxRepository: BlockchainTxRepository,
) : SimpleViewModel(),
    TransferTokensDialogHandler by transferTokensDialogHandler,
    LimitedGasDialogHandler by limitedGasDialogHandler {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private var gasLimitCalculateJob: Job? = null

    init {
        walletRepository.snps.onEach { walletModel ->
            if (walletModel != null) loadLegacyGasPrice(walletModel)
            _uiState.update { it.copy(snpWalletModel = walletModel) }
        }.launchIn(viewModelScope)
    }

    private fun loadLegacyGasPrice(snpWalletModel: WalletModel) {
        viewModelScope.launch {
            action.execute {
                blockchainTxRepository.getLegacyGasPrice(snpWalletModel)
            }.doOnSuccess { gasPrice -> _uiState.update { it.copy(gasPrice = gasPrice) } }
        }
    }

    fun onMaxButtonClicked() {
        onAmountValueChanged(_uiState.value.snpWalletModel?.coinValue?.value?.stripTrailingZeros().orEmpty())
    }

    fun onAmountValueChanged(value: String) {
        _uiState.update {
            it.copy(
                amountValue = value,
                total = CoinSNPS(value.stringAmountToDoubleSafely() ?: 0.0)
                    .toFiat(walletRepository.snpsAccountState.value.dataOrCache?.snpsUsdExchangeRate ?: 0.0),
            )
        }
        scheduleGasLimitCalculate()
    }

    private fun scheduleGasLimitCalculate() {
        val snpWalletModel = _uiState.value.snpWalletModel ?: return
        gasLimitCalculateJob?.cancel()
        val value = getValueDecimalApplied()
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

    private fun getValueDecimalApplied(): BigInteger? = _uiState.value.amountValue
        .applyDecimal(CoinType.SNPS.decimal)
        ?.takeIf { it > BigInteger.ZERO }

    fun onCardNumberValueChanged(value: String) {
        _uiState.update { it.copy(cardNumberValue = value) }
    }

    fun onRepeatCardNumberValueChanged(value: String) {
        _uiState.update { it.copy(repeatCardNumberValue = value) }
    }

    fun onSendClicked() {
        val amount = requireNotNull(getValueDecimalApplied())
        val snpWalletModel = _uiState.value.snpWalletModel ?: return
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
            }.flatMap { hash ->
                walletRepository.confirmPayout(
                    amount = _uiState.value.amountValue.stringAmountToDouble(),
                    cardNumber = _uiState.value.cardNumberValue.digitsOnly(),
                ).map {
                    hash
                }
            }
        }.doOnSuccess {
            _command publish Command.CloseScreenOnSuccess(
                data = TransferTokensSuccessData(txHash = it, type = TransferTokensSuccessData.Type.Sell)
            )
        }.doOnComplete {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val isCalculating: Boolean = false,
        val snpWalletModel: WalletModel? = null,
        val amountValue: String = "",
        val cardNumberValue: String = "",
        val repeatCardNumberValue: String = "",
        val commission: Int = 2,
        val total: FiatValue = FiatUSD(0.0),

        val amountFormatter: SimpleFormatter = BigAmountFormatter(),
        val cardNumberFormatter: SimpleFormatter = CardNumberFormatter(),

        val gasPrice: Long? = null,
        val gasLimit: Long = gasLimitNull,
    ) {

        val isSendEnabled: Boolean
            get() = !isCalculating
                && gasLimit != gasLimitNull
                && amountValue.stringAmountToDoubleOrZero() > 0.0
                && cardNumberValue.digitsOnly().length == 16
                && cardNumberValue.digitsOnly() == repeatCardNumberValue.digitsOnly()
    }

    sealed interface Command {
        data class CloseScreenOnSuccess(val data: TransferTokensSuccessData) : Command
    }
}