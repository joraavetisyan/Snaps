package io.snaps.corecommon.strings

import io.snaps.corecommon.model.CryptoAddress
import java.util.Locale

val DEFAULT_LOCALE: Locale get() = Locale.getDefault()
val RU_LOCALE: Locale get() = Locale("ru")

val emojis = listOf("😁", "🥰", "😂", "😳", "😏", "😅", "🥺", "😌", "😬")

enum class PluralKey {
    Zero, One, Two, Few, Many, Other;

    companion object {
        fun get(name: String) = values().firstOrNull { it.name.equals(name, ignoreCase = true) }
    }
}

enum class SupportedLanguageKey {
    Ru, En, Tr, Ua, Es;

    companion object {
        val Default = En
        fun get(name: String) = values().firstOrNull { it.name.equals(name, ignoreCase = true) }
    }
}

fun Locale.toSupportedLanguageKey() =
    SupportedLanguageKey.get(country) ?: SupportedLanguageKey.Default

internal fun key(stringKey: StringKey) = stringKey.name
internal fun key(stringKey: StringKey, pluralKey: PluralKey) = "${stringKey.name}_${pluralKey.name}"

fun String.addPrefix(prefix: String, ignoreEmpty: Boolean = false): String {
    if (this.isEmpty() && ignoreEmpty) return this
    else if (this.startsWith(prefix)) return this
    return "$prefix$this"
}

val CryptoAddress.addressEllipsized get() = take(7) + "..." + takeLast(10)

val String.approximated get() = "~ $this"

fun String.capitalizeFirstLetter() = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

fun String.digitsOnly() = filter { it.isDigit() }

fun String.isUserNameValid() = Regex("^\\p{L}+[\\p{L}\\p{Nd}_.]*$").matches(this)