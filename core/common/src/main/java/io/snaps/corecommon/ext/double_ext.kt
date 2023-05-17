package io.snaps.corecommon.ext

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

fun Double.round(places: Int = 2): Double {
    require(places >= 0)
    return BigDecimal.valueOf(this)
        .setScale(places, RoundingMode.HALF_UP)
        .toDouble()
}

fun Double.toPercentageFormat() = "${(this * 100).round(1).toStringValue()}%"

fun Double.toStringValue(): String {
    var number = this.toBigDecimal().toPlainString()
    if (number.endsWith(".0")) {
        number = number.removeSuffix(".0")
    }
    return number
}

fun BigDecimal.toStringValue(): String {
    var number = this.toPlainString()
    while (number.last() == '0' || number.last() == '.') {
        number = number.removeSuffix("0")
        number = number.removeSuffix(".")
    }
    return number
}

fun Double.coinToFormatDecimal(): String {
    val number = this.round(3)
    if (number.toInt() >= 100000) {
        return number.toInt().toFormatDecimal(Locale.US)
    }
    return number.toStringValue()
}