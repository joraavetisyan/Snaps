package io.snaps.corecommon.ext

import android.icu.text.CompactDecimalFormat
import io.snaps.corecommon.strings.DEFAULT_LOCALE
import java.util.Locale

fun Int.toFormatDecimal(locale: Locale = DEFAULT_LOCALE): String {
    return CompactDecimalFormat
        .getInstance(locale, CompactDecimalFormat.CompactStyle.SHORT)
        .format(this)
}

fun String.parseToDouble(): Double {
    return this.replace(",", ".")
        .filter { it.isDigit() || it == '.' }
        .toDouble()
}