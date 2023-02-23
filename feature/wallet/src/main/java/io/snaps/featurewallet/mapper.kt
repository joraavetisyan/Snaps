package io.snaps.featurewallet

import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.WalletModel
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.LeftPart
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart

fun List<WalletModel>.toCellTileStateList(
    onClick: ((WalletModel) -> Unit)? = null,
): List<CellTileState> = map { it.toCellTileState(onClick) }

fun WalletModel.toCellTileState(
    onClick: ((WalletModel) -> Unit)?,
) = CellTileState(
    middlePart = MiddlePart.Data(
        value = symbol.textValue(),
    ),
    leftPart = LeftPart.Logo(ImageValue.Url(iconUrl)),
    rightPart = RightPart.TextMoney(
        coin = coinValue,
        fiatCurrency = this.fiatValue,
    ),
    clickListener = onClick?.let { { it.invoke(this) } },
)