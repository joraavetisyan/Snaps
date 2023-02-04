package io.snaps.featurewallet.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.CurrencyType
import io.snaps.corecommon.model.MoneyDto
import io.snaps.corecommon.R
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.LeftPart
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
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
) : SimpleViewModel(), MainHeaderHandler by mainHeaderHandlerDelegate {

    private val _uiState = MutableStateFlow(
        UiState(
            currencies = getCurrencies(),
            selectCurrencyBottomDialogItems = getSelectCurrencyBottomDialogItems(),
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onTopUpClicked() = viewModelScope.launch {
        _uiState.update {
            it.copy(bottomDialogType = BottomDialogType.TopUp)
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

    private fun getCurrencies() = listOf(
        CellTileState(
            middlePart = MiddlePart.Data(
                value = CurrencyType.BNB.name.textValue(),
            ),
            leftPart = LeftPart.Logo(ImageValue.ResImage(R.drawable.ic_bnb_token)),
            rightPart = RightPart.TextMoney(
                balance = MoneyDto(CurrencyType.BNB, 0.0),
                toCurrency = MoneyDto(CurrencyType.USD, 0.0),
            ),
        ),
        CellTileState(
            middlePart = MiddlePart.Data(
                value = CurrencyType.SNP.name.textValue(),
            ),
            leftPart = LeftPart.Logo(ImageValue.ResImage(R.drawable.ic_bnb_token)),
            rightPart = RightPart.TextMoney(
                balance = MoneyDto(CurrencyType.SNP, 0.0),
                toCurrency = MoneyDto(CurrencyType.USD, 0.0),
            ),
        ),
        CellTileState(
            middlePart = MiddlePart.Data(
                value = CurrencyType.SNPS.name.textValue(),
            ),
            leftPart = LeftPart.Logo(ImageValue.ResImage(R.drawable.ic_bnb_token)),
            rightPart = RightPart.TextMoney(
                balance = MoneyDto(CurrencyType.SNPS, 0.0),
                toCurrency = MoneyDto(CurrencyType.USD, 0.0),
            ),
        ),
        CellTileState(
            middlePart = MiddlePart.Data(
                value = CurrencyType.BUSD.name.textValue(),
            ),
            leftPart = LeftPart.Logo(ImageValue.ResImage(R.drawable.ic_bnb_token)),
            rightPart = RightPart.TextMoney(
                balance = MoneyDto(CurrencyType.BUSD, 0.0),
                toCurrency = MoneyDto(CurrencyType.USD, 0.0),
            ),
        ),
    )

    private fun getSelectCurrencyBottomDialogItems(): List<CellTileState> {
        return listOf(
            CellTileState(
                middlePart = MiddlePart.Data(
                    value = CurrencyType.BNB.name.textValue(),
                ),
                leftPart = LeftPart.Logo(ImageValue.ResImage(R.drawable.ic_bnb_token)),
                rightPart = RightPart.NavigateNextIcon(
                    text = MoneyDto(CurrencyType.BUSD, 0.0).getFormattedMoneyWithCurrency().textValue(),
                ),
                clickListener = { onSelectCurrencyBottomDialogItemClicked(MoneyDto(CurrencyType.BUSD, 0.0)) },
            ),
            CellTileState(
                middlePart = MiddlePart.Data(
                    value = CurrencyType.SNP.name.textValue(),
                ),
                leftPart = LeftPart.Logo(ImageValue.ResImage(R.drawable.ic_bnb_token)),
                rightPart = RightPart.NavigateNextIcon(
                    text = MoneyDto(CurrencyType.SNP, 0.0).getFormattedMoneyWithCurrency().textValue(),
                ),
                clickListener = { onSelectCurrencyBottomDialogItemClicked(MoneyDto(CurrencyType.SNP, 0.0)) }
            ),
            CellTileState(
                middlePart = MiddlePart.Data(
                    value = CurrencyType.SNPS.name.textValue(),
                ),
                leftPart = LeftPart.Logo(ImageValue.ResImage(R.drawable.ic_bnb_token)),
                rightPart = RightPart.NavigateNextIcon(
                    text = MoneyDto(CurrencyType.SNPS, 0.0).getFormattedMoneyWithCurrency().textValue(),
                ),
                clickListener = { onSelectCurrencyBottomDialogItemClicked(MoneyDto(CurrencyType.SNPS, 0.0)) }
            ),
            CellTileState(
                middlePart = MiddlePart.Data(
                    value = CurrencyType.BUSD.name.textValue(),
                ),
                leftPart = LeftPart.Logo(ImageValue.ResImage(R.drawable.ic_bnb_token)),
                rightPart = RightPart.NavigateNextIcon(
                    text = MoneyDto(CurrencyType.BUSD, 0.0).getFormattedMoneyWithCurrency().textValue(),
                ),
                clickListener = { onSelectCurrencyBottomDialogItemClicked(MoneyDto(CurrencyType.BUSD, 0.0)) },
            ),
        )
    }

    private fun onSelectCurrencyBottomDialogItemClicked(item: MoneyDto) = viewModelScope.launch {
        _command publish Command.HideBottomDialog
        _uiState.update {
            it.copy(selectedCurrency = item)
        }
    }

    data class UiState(
        val token: String = "0x3EBAc5s...632EX67FS54",
        val selectedCurrency: MoneyDto = MoneyDto(currency = CurrencyType.BNB, value = 0.0),
        val currencies: List<CellTileState>,
        val selectCurrencyBottomDialogItems: List<CellTileState>,
        val bottomDialogType: BottomDialogType = BottomDialogType.SelectCurrency,
    )

    enum class BottomDialogType {
        SelectCurrency, TopUp,
    }

    sealed class Command {
        object OpenWithdrawScreen : Command()
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
    }
}
