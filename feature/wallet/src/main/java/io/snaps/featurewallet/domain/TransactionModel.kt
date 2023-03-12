package io.snaps.featurewallet.domain

import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.model.Uuid
import java.time.LocalDateTime

data class TransactionModel(
    val id: Uuid,
    val date: LocalDateTime,
    val symbol: String,
    val icon: ImageValue,
    val coinValue: String,
)