package io.snaps.featurewallet

import io.snaps.baseprofile.domain.BalanceModel
import io.snaps.basewallet.data.model.PayoutOrderResponseDto
import io.snaps.basewallet.data.model.PayoutOrderStatus
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.round
import io.snaps.corecommon.ext.toStringValue
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.model.WalletModel
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
): List<CellTileState> = map { it.toCellTileState(onClick = onClick) }

fun WalletModel.toCellTileState(
    onClick: ((WalletModel) -> Unit)?,
) = CellTileState(
    middlePart = MiddlePart.Data(
        value = symbol.textValue(),
    ),
    leftPart = if (symbol == "SNAPS") {
        ImageValue.ResImage(R.drawable.ic_snp_token)
    } else {
        ImageValue.Url(iconUrl)
    }.let(LeftPart::Logo),
    rightPart = RightPart.TextMoney(
        coin = coinValue,
        fiatCurrency = fiatValue,
    ),
    clickListener = onClick?.let { { it.invoke(this) } },
)

fun State<BalanceModel>.toRewardsTileState(
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

fun BalanceModel.toRewardsTileState() = listOf(
    RewardsTileState.Unlocked(
        unlockedTokensBalance = unlocked.round().toStringValue(),
        balanceInUsd = (unlocked * snpExchangeRate).round().toStringValue(),
    ),
    RewardsTileState.Locked(
        lockedTokensBalance = locked.round().toStringValue(),
        balanceInUsd = (locked * snpExchangeRate).round().toStringValue(),
    ),
)

fun List<TransactionModel>.toTransactionList(
    onTransactionClicked: (TransactionModel) -> Unit,
) = map { it.toTransactionTile(onTransactionClicked) }

fun TransactionModel.toTransactionTile(
    onClicked: (TransactionModel) -> Unit,
) = TransactionTileState.Data(
    id = id,
    icon = ImageValue.ResImage(R.drawable.ic_snp_token),
    type = type,
    coins = "${balanceChange.round().toStringValue()} SNP".textValue(),
    dateTime = date.toStringValue(),
    clickListener = { onClicked(this) },
)

private fun LocalDateTime.toStringValue(): TextValue {
    return format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")).textValue()
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
                    PayoutOrderStatus.Success -> PayoutStatusState.Data.Success(
                        dto = it,
                        onCopyClick = onCopyClick,
                    )
                }
            }
        } else {
            null
        }
    }
}