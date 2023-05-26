package io.snaps.corecommon.date

import io.snaps.corecommon.strings.DEFAULT_LOCALE
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

object LocalDateFactory {

    /**
     * The string must represent a valid date and is parsed using
     * {@link java.time.format.DateTimeFormatter#ISO_LOCAL_DATE}.
     *
     * @param date  the text to parse such as "2007-12-03", not null
     * @return the parsed local date
     */
    fun from(date: String) = runCatching {
        LocalDate.parse(date)
    }.getOrNull()

    /**
     * Obtains an instance of {@code LocalDate} from a text string in a specified format.
     *
     * @param date the text to parse, not null
     * @param format  the format of date, not null
     * @return the parsed local date
     */
    fun from(date: String, format: DateTimeFormat) = from(date, format.code)

    fun from(date: String, format: String) = runCatching {
        LocalDate.parse(date, format.toDateTimeFormat(DEFAULT_LOCALE))
    }.getOrNull()
}

fun ZonedDateTime.toOffsetLocalDateTime(
    zoneId: ZoneId = ZoneId.systemDefault(),
) = LocalDateTime.ofInstant(this.toInstant(), zoneId)