package io.snaps.corecommon.ext

import android.icu.text.CompactDecimalFormat
import io.snaps.corecommon.strings.DEFAULT_LOCALE
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Locale

fun Int.toCompactDecimalFormat(locale: Locale = DEFAULT_LOCALE): String {
    return CompactDecimalFormat
        .getInstance(locale, CompactDecimalFormat.CompactStyle.SHORT)
        .format(this)
}

fun String.stringAmountToDoubleSafely() = filter {
    it.isDigit() || it == ',' || it == '.'
}.replace(',', '.').run {
    if (count { it == '.' } > 1) {
        replaceFirst(".", "")
    } else this
}.toDoubleOrNull()

fun String.stringAmountToDoubleOrZero() = stringAmountToDoubleSafely() ?: 0.0

fun String.stringAmountToDouble() = stringAmountToDoubleSafely()!!

fun String.applyDecimal(decimal: Int): BigInteger? = this
    .stringAmountToDoubleSafely()
    ?.applyDecimal(decimal)

fun Double.applyDecimal(decimal: Int): BigInteger = this
    .toBigDecimal()
    .movePointRight(decimal)
    .toBigInteger()

fun Long.unapplyDecimal(decimal: Int): BigDecimal = this
    .toBigDecimal()
    .movePointLeft(decimal)