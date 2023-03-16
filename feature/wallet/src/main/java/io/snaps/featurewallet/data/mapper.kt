package io.snaps.featurewallet.data

import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.date.toOffsetLocalDateTime
import io.snaps.featurewallet.data.model.BalanceResponseDto
import io.snaps.featurewallet.data.model.TransactionItemResponseDto
import io.snaps.featurewallet.domain.RewardModel
import io.snaps.featurewallet.domain.TransactionModel
import java.time.ZonedDateTime

fun List<TransactionItemResponseDto>.toModelList() = map(TransactionItemResponseDto::toTransactionModel)

fun TransactionItemResponseDto.toTransactionModel() = TransactionModel(
    id = id,
    icon = ImageValue.Url(iconUrl),
    date = requireNotNull(ZonedDateTime.parse(date)).toOffsetLocalDateTime(),
    coinValue = coinValue,
    symbol = symbol,
)

fun BalanceResponseDto.toRewardModel() = RewardModel(
    unlockedTokensBalance = unlockedTokensBalance,
    lockedTokensBalance = lockedTokensBalance,
)