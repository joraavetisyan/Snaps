package io.snaps.basenft.ui

import io.snaps.corecommon.ext.round
import io.snaps.corecommon.model.Fiat

fun Int.costToString() = if (this == 0) "Free" else "$this ${Fiat.Currency.USD.symbol}"

fun Int.dailyRewardToString() = "~ ${(this / 100.0).round(1)} ${Fiat.Currency.USD.symbol}"