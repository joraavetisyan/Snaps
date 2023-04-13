package io.snaps.featurewallet.domain

import io.snaps.baseprofile.data.model.TransactionType
import io.snaps.corecommon.model.Uuid
import java.time.LocalDateTime

data class TransactionModel(
    val id: Uuid,
    val date: LocalDateTime,
    val balanceChange: Double,
    val type: TransactionType,
)