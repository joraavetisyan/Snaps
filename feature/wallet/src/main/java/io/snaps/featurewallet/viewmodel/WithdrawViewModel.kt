package io.snaps.featurewallet.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.basesources.NotificationsSource
import io.snaps.basewallet.data.WalletRepository
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.WalletModel
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.viewmodel.SimpleViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class WithdrawViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    mainHeaderHandlerDelegate: MainHeaderHandler,
    sendHandlerDelegate: CryptoSendHandler,
    walletRepository: WalletRepository,
    private val notificationsSource: NotificationsSource,
) : SimpleViewModel(),
    MainHeaderHandler by mainHeaderHandlerDelegate,
    CryptoSendHandler by sendHandlerDelegate {

    private val args = savedStateHandle.requireArgs<AppRoute.Withdraw.Args>()

    private val _uiState = MutableStateFlow(
        UiState(
            walletModel = args.wallet,
            availableAmount = "${walletRepository.getAvailableBalance(args.wallet)} ${args.wallet.symbol}",
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onAmountValueChanged(amount: String) {
        disableSend()
        _uiState.update { it.copy(amountValue = amount) }
    }

    fun onAddressValueChanged(address: String) {
        disableSend()
        _uiState.update { it.copy(addressValue = address) }
    }

    fun onMaxButtonClicked() {
        disableSend()
        _uiState.update { state ->
            state.copy(amountValue = state.availableAmount.filter { it.isDigit() || it == '.' })
        }
    }

    fun onConfirmTransactionClicked() {
        val amountBigDecimal = _uiState.value.amountValue.toBigDecimalOrNull()?.takeIf {
            it > BigDecimal.ZERO
        }?.movePointRight(args.wallet.decimal)?.toBigInteger() ?: kotlin.run {
            viewModelScope.launch {
                notificationsSource.sendError("Invalid amount".textValue())
            }
            return
        }
        onSendClicked(
            scope = viewModelScope,
            wallet = args.wallet,
            address = _uiState.value.addressValue,
            amount = amountBigDecimal,
        )
    }

    data class UiState(
        val walletModel: WalletModel,
        val addressValue: String = "",
        val amountValue: String = "",
        val availableAmount: String = "",
    ) {

        val isConfirmEnabled: Boolean get() = addressValue.isNotBlank() && amountValue.isNotBlank()
    }

    sealed interface Command
}