package io.snaps.featurewallet.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.basewallet.data.WalletRepository
import io.snaps.corecommon.model.CurrencyType
import io.snaps.corecommon.model.MoneyDto
import io.snaps.coreui.barcode.BarcodeManager
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.featurewallet.toCellTileStateList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    mainHeaderHandlerDelegate: MainHeaderHandler,
    private val walletRepository: WalletRepository,
    private val barcodeManager: BarcodeManager,
) : SimpleViewModel(), MainHeaderHandler by mainHeaderHandlerDelegate {

    private val _uiState = MutableStateFlow(
        UiState(
            address = walletRepository.getActiveWalletsReceiveAddresses().firstOrNull().orEmpty(),
            currencies = walletRepository.getActiveWallets().toCellTileStateList(),
            selectCurrencyBottomDialogItems = emptyList(),
        )
    )

    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onTopUpClicked() = viewModelScope.launch {
        _uiState.update {
            val qr = barcodeManager.getQrCodeBitmap(it.address)
            it.copy(bottomDialogType = BottomDialogType.TopUp(qr))
        }
        _command publish Command.ShowBottomDialog
    }

    fun onWithdrawClicked() = viewModelScope.launch {
        _command publish Command.OpenWithdrawScreen
    }

    fun onExchangeClicked() = viewModelScope.launch {
        _uiState.update {
            it.copy(bottomDialogType = BottomDialogType.SelectCurrency)
        }
        _command publish Command.ShowBottomDialog
    }

    private fun onSelectCurrencyBottomDialogItemClicked(item: MoneyDto) = viewModelScope.launch {
        _command publish Command.HideBottomDialog
        _uiState.update { it.copy(selectedCurrency = item) }
    }

    data class UiState(
        val address: String,
        val selectedCurrency: MoneyDto = MoneyDto(currency = CurrencyType.USD, value = 0.0),
        val currencies: List<CellTileState>,
        val selectCurrencyBottomDialogItems: List<CellTileState>,
        val bottomDialogType: BottomDialogType = BottomDialogType.SelectCurrency,
    )

    sealed class BottomDialogType {
        object SelectCurrency : BottomDialogType()
        data class TopUp(val qr: Bitmap?) : BottomDialogType()
    }

    sealed class Command {
        object OpenWithdrawScreen : Command()
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
    }
}