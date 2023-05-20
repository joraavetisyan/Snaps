package io.snaps.corecommon.ext

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

fun Double.round(places: Int = 2): Double {
    require(places >= 0)
    return BigDecimal.valueOf(this)
        .setScale(places, RoundingMode.HALF_UP)
        .toDouble()
}

fun Double.toNumberFormat(): String = try {
    NumberFormat.getNumberInstance(Locale.US).format(round())
} catch (ex: NumberFormatException) {
    this.toString()
}

fun Double.toCompactDecimalFormat(): String {
    val number = round(3)
    number.toInt().let {
        if (it >= 100000) {
            return it.toCompactDecimalFormat(Locale.US)
        }
    }
    return number.stripUselessDecimals()
}

private fun Double.stripUselessDecimals(): String {
    var number = toBigDecimal().toPlainString()
    if (number.endsWith(".0")) {
        number = number.removeSuffix(".0")
    }
    return number
}

fun Double.toPercentageFormat() = "${(this * 100).round(1).stripUselessDecimals()}%"