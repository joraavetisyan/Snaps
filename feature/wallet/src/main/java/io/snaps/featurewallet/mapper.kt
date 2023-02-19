package io.snaps.featurewallet

import io.snaps.basewallet.domain.WalletModel
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.CurrencyType
import io.snaps.corecommon.model.MoneyDto
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.LeftPart
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart

fun List<WalletModel>.toCellTileStateList(): List<CellTileState> = map {
    toCellTileState(it)
}

fun toCellTileState(it: WalletModel) = CellTileState(
    middlePart = MiddlePart.Data(
        value = it.symbol.textValue(),
    ),
    leftPart = LeftPart.Logo(ImageValue.Url(it.iconUrl)),
    rightPart = RightPart.TextMoney(
        balance = MoneyDto(CurrencyType.USD, 0.0),
        toCurrency = MoneyDto(CurrencyType.USD, 0.0),
    ),
)