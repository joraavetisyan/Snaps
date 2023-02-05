package io.snaps.featurewallet.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.corecommon.model.CurrencyType
import io.snaps.corecommon.model.MoneyDto
import io.snaps.coreui.viewmodel.SimpleViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class WithdrawViewModel @Inject constructor(
    mainHeaderHandlerDelegate: MainHeaderHandler,
) : SimpleViewModel(), MainHeaderHandler by mainHeaderHandlerDelegate {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onAmountValueChanged(amount: String) {
        _uiState.update {
            it.copy(amountValue = amount)
        }
    }

    fun onAddressValueChanged(address: String) {
        _uiState.update {
            it.copy(addressValue = address)
        }
    }

    fun onConfirmTransactionClicked() {}

    data class UiState(
        val addressValue: String = "",
        val amountValue: String = "",
        val availableAmount: MoneyDto = MoneyDto(CurrencyType.BNB, 0.0),
        val transactionFee: MoneyDto = MoneyDto(CurrencyType.BNB, 0.0),
        val totalAmount: MoneyDto = MoneyDto(CurrencyType.BNB, 0.0),
    )

    sealed class Command
}