package io.snaps.featurewallet

import io.snaps.basewallet.domain.SnpsAccountModel
import io.snaps.basewallet.data.model.PayoutOrderResponseDto
import io.snaps.basewallet.data.model.PayoutOrderStatus
import io.snaps.corecommon.R
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.round
import io.snaps.corecommon.model.CoinSNPS
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.corecommon.model.Uuid
import io.snaps.basewallet.domain.WalletModel
import io.snaps.corecommon.model.CoinType
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.LeftPart
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
import io.snaps.featurewallet.domain.TransactionModel
import io.snaps.featurewallet.screen.PayoutStatusState
import io.snaps.featurewallet.screen.RewardsTileState
import io.snaps.featurewallet.screen.TransactionTileState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun List<WalletModel>.toCellTileStateList(
    onClick: ((WalletModel) -> Unit)? = null,
): List<CellTileState> = sortedBy { it.coinType.ordinal }.map { it.toCellTileState(onClick = onClick) }

fun WalletModel.toCellTileState(
    onClick: ((WalletModel) -> Unit)?,
) = CellTileState(
    middlePart = MiddlePart.Data(
        value = coinType.symbol.textValue(),
    ),
    leftPart = LeftPart.Logo(image),
    rightPart = RightPart.TextMoney(
        coin = coinValue,
        fiat = fiatValue,
    ),
    clickListener = onClick?.let { { it.invoke(this) } },
)

fun State<SnpsAccountModel>.toRewardsTileState(
    onRewardReloadClicked: () -> Unit,
): List<RewardsTileState> {
    return when (this) {
        is Effect -> when {
            isSuccess -> requireData.toRewardsTileState()
            else -> listOf(RewardsTileState.Error(clickListener = onRewardReloadClicked))
        }
        is Loading -> List(2) { RewardsTileState.Shimmer }
    }
}

fun SnpsAccountModel.toRewardsTileState() = listOf(
    RewardsTileState.Unlocked(
        balance = unlocked,
        fiatValue = unlockedInFiat,
    ),
    RewardsTileState.Locked(
        balance = locked,
        fiatValue = lockedInFiat,
    ),
)

fun List<TransactionModel>.toTransactionList(
    onTransactionClicked: (TransactionModel) -> Unit,
) = map { it.toTransactionTile(onTransactionClicked) }

fun TransactionModel.toTransactionTile(
    onClicked: (TransactionModel) -> Unit,
) = TransactionTileState.Data(
    id = id,
    icon = R.drawable.ic_snp_token.imageValue(),
    type = type,
    value = CoinSNPS(balanceChange.round()),
    dateTime = date.toStringValue(),
    clickListener = { onClicked(this) },
)

private fun LocalDateTime.toStringValue(): TextValue {
    return format(DateTimeFormatter.ofPattern("dd MMMM HH:mm")).textValue()
}

fun State<List<PayoutOrderResponseDto>>.toPayoutStatusState(
    onContactSupportClick: () -> Unit,
    onCopyClick: (Uuid) -> Unit,
): PayoutStatusState? {
    return when (this) {
        is Loading -> PayoutStatusState.Shimmer
        is Effect -> if (isSuccess) {
            requireData.lastOrNull()?.let {
                when (it.status) {
                    PayoutOrderStatus.InProcess -> PayoutStatusState.Data.Processing(
                        dto = it,
                        onCopyClick = onCopyClick,
                    )
                    PayoutOrderStatus.Rejected -> PayoutStatusState.Data.Rejected(
                        dto = it,
                        onContactSupportClick = onContactSupportClick,
                        onCopyClick = onCopyClick,
                    )
                    PayoutOrderStatus.Success -> PayoutStatusState.Success
                }
            }
        } else {
            null
        }
    }
}