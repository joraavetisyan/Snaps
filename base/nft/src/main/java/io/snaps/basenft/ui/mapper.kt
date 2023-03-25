package io.snaps.basenft.ui

import io.snaps.corecommon.model.FiatCurrency

fun Int.costToString() = if (this == 0) "Free" else "$this${FiatCurrency.USD.symbol}"