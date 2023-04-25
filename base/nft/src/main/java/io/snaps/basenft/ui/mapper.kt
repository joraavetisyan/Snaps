package io.snaps.basenft.ui

import io.snaps.corecommon.ext.round
import io.snaps.corecommon.model.FiatCurrency

fun Int.costToString() = if (this == 0) "Free" else "$this ${FiatCurrency.USD.symbol}"

fun Int.dailyRewardToString() = "~ ${(this / 100.0).round(1)} ${FiatCurrency.USD.symbol}"