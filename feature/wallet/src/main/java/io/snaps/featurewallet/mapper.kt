package io.snaps.featurewallet

import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.WalletModel
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.LeftPart
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
import io.snaps.featurewallet.domain.TransactionModel
import io.snaps.featurewallet.screen.TransactionTileState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

fun List<TransactionModel>.toTransactionList(
    onTransactionClicked: (TransactionModel) -> Unit,
) = map { it.toTransactionTile(onTransactionClicked) }

fun TransactionModel.toTransactionTile(
    onClicked: (TransactionModel) -> Unit,
) = TransactionTileState.Data(
    id = id,
    icon = icon,
    coinSymbol = symbol.textValue(),
    coins = coinValue.textValue(),
    dateTime = date.toStringValue(),
    clickListener = { onClicked(this) },
)

private fun LocalDateTime.toStringValue(): TextValue {
    return format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")).textValue()
}