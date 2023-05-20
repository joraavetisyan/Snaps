package io.snaps.corecommon.ext

import io.snaps.corecommon.strings.DEFAULT_LOCALE
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat

fun Double.round(places: Int = 2): Double {
    require(places >= 0)
    return BigDecimal.valueOf(this)
        .setScale(places, RoundingMode.HALF_UP)
        .toDouble()
}

fun Double.toMoneyFormat(roundPlaces: Int = 2): String = try {
    NumberFormat.getNumberInstance(DEFAULT_LOCALE).format(round(roundPlaces)).stripUselessDecimals()
} catch (ex: NumberFormatException) {
    this.toString()
}

/**
 * eg 2000000.0 -> 2M
 */
fun Double.toCompactDecimalFormat(): String {
    val number = round(3)
    number.toInt().let {
        if (it >= 100_000) {
            return it.toCompactDecimalFormat()
        }
    }
    return number.stripUselessDecimals()
}

fun Double.stripUselessDecimals(): String = toBigDecimal().toPlainString().stripUselessDecimals()

private fun String.stripUselessDecimals(): String = if (contains('.')) {
    var s = this
    while (s.endsWith("0")) {
        s = s.removeSuffix("0")
    }
    s.removeSuffix(".")
} else {
    this
}

fun Double.toPercentageFormat() = "${(this * 100).round(1).stripUselessDecimals()}%"