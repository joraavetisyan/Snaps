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

fun Double.toMoneyFormat(roundPlaces: Int): String = try {
    NumberFormat.getNumberInstance(DEFAULT_LOCALE).apply {
        maximumFractionDigits = Int.MAX_VALUE
    }.format(round(roundPlaces))
} catch (ex: NumberFormatException) {
    log(ex)
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
    return number.stripTrailingZeros()
}

fun Double.stripTrailingZeros(): String = toBigDecimal().stripTrailingZeros().toPlainString()

fun Double.toPercentageFormat() = "${(this * 100).round(1).stripTrailingZeros()}%"