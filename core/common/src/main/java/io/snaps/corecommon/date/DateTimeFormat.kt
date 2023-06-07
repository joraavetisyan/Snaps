package io.snaps.corecommon.date

import android.text.format.DateUtils.DAY_IN_MILLIS
import android.text.format.DateUtils.SECOND_IN_MILLIS
import android.text.format.DateUtils.getRelativeTimeSpanString
import io.snaps.corecommon.strings.DEFAULT_LOCALE
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

enum class DateTimeFormat(val code: String) {

    ISO_LOCAL_DATE("yyyy-MM-dd"),

    ISO_LOCAL_TIME("HH:mm:ss"),

    ISO_LOCAL_DATE_TIME("yyyy-MM-dd'T'HH:mm"),

    DATE_TIME_DASH_FORMAT("yyyyMMdd_HHmmss"),

    ISO_LOCAL_DATE_TIME_FULL("yyyy-MM-dd'T'HH:mm[:ss][.SSS]"),
    // XXX - 03:00
    ISO_OFFSET_DATE_TIME("yyyy-MM-dd'T'HH:mm[:ss][.SSS][XXX]"),
    // Z - 0300
    ISO_ZONED_DATE_TIME("yyyy-MM-dd'T'HH:mm[:ss][.SSS]Z");

    fun toDateTimeFormat(locale: Locale = DEFAULT_LOCALE) = code.toDateTimeFormat(locale)
}

fun String.toDateTimeFormat(locale: Locale = DEFAULT_LOCALE) = kotlin.runCatching {
    DateTimeFormatter.ofPattern(this, locale)
}.getOrNull()

fun getLocaleDateByPhotoDateFormat(): String {
    return SimpleDateFormat(DateTimeFormat.DATE_TIME_DASH_FORMAT.code, Locale.getDefault()).format(
        Date()
    )
}

fun LocalDateTime.toStringValue(): String = when {
    Period.between(this.toLocalDate(), LocalDate.now()).days <= 1 -> getRelativeTimeSpanString(
            toEpochMilli(), System.currentTimeMillis(), SECOND_IN_MILLIS
    ).toString()
    Period.between(this.toLocalDate(), LocalDate.now()).days <= 7 -> getRelativeTimeSpanString(
            toEpochMilli(), System.currentTimeMillis(), DAY_IN_MILLIS
    ).toString()
    this.year == LocalDate.now().year -> toLocalDate().format("MMMM dd")
    else -> toLocalDate().format("MMMM dd, yyyy")
}

fun Duration.toTimeFormat() = inWholeMilliseconds.toTimeFormat()

private fun Long.toTimeFormat(): String {
    val hours = TimeUnit.MILLISECONDS.toHours(this)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(hours)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) -
            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this))
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

private fun LocalDate.format(pattern: String) = this.format(DateTimeFormatter.ofPattern(pattern))