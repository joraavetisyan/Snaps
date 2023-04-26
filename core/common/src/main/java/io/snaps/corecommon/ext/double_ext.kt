package io.snaps.corecommon.ext

import java.math.BigDecimal
import java.math.RoundingMode

fun Double.round(places: Int = 2): Double {
    require(places >= 0)
    return BigDecimal.valueOf(this)
        .setScale(places, RoundingMode.HALF_UP)
        .toDouble()
}

fun Double.toPercentageFormat() = "${(this * 100).round(1).toStringValue()} %"

fun Double.toStringValue(): String {
    var number = this.toString()
    if (number.last() == '0') {
        number = number.removeSuffix(".0")
    }
    return number
}