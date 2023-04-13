package io.snaps.featurewallet.data

import io.snaps.baseprofile.data.model.TransactionItemResponseDto
import io.snaps.corecommon.date.toOffsetLocalDateTime
import io.snaps.featurewallet.domain.TransactionModel
import java.time.ZonedDateTime

fun List<TransactionItemResponseDto>.toModelList() =
    map(TransactionItemResponseDto::toTransactionModel)

private fun TransactionItemResponseDto.toTransactionModel() = TransactionModel(
    id = id,
    date = requireNotNull(ZonedDateTime.parse(date)).toOffsetLocalDateTime(),
    balanceChange = balanceChange,
    type = type,
)