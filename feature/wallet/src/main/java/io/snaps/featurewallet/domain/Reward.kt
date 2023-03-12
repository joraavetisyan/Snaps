package io.snaps.featurewallet.domain

import io.snaps.corecommon.model.CurrencySymbol
import io.snaps.corecommon.model.DateTime

data class Reward(
    val coin: String,
    val fiat: String,
    val currencySymbol: CurrencySymbol,
    val dateUnlock: DateTime,
)