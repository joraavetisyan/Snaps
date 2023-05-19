@file:Suppress("DEPRECATION")

package io.snaps.corecommon.container

import androidx.compose.ui.text.AnnotatedString
import io.snaps.corecommon.strings.StringKey

private const val message = "Constructor usage is deprecated, use extension function instead"
private const val import = "io.snaps.corecommon.container.textValue"

sealed class TextValue {

    data class Simple @Deprecated(
        message = message,
        replaceWith = ReplaceWith("value.textValue()", import),
    ) constructor(
        val value: String,
    ) : TextValue()

    data class Annotated @Deprecated(
        message = message,
        replaceWith = ReplaceWith("value.textValue()", import),
    ) constructor(
        val value: AnnotatedString,
    ) : TextValue()

    data class Res @Deprecated(
        message = message,
        replaceWith = ReplaceWith("value.textValue(args = args)", import),
    ) constructor(
        val value: StringKey,
        val args: List<Any> = emptyList(),
    ) : TextValue()

    data class Plural @Deprecated(
        message = message,
        replaceWith = ReplaceWith("value.textValue(quantity = quantity, args = args)", import),
    ) constructor(
        val value: StringKey,
        val quantity: Int,
        val args: List<Any> = listOf(quantity),
    ) : TextValue()
}

fun String.textValue(): TextValue = TextValue.Simple(this)

fun AnnotatedString.textValue(): TextValue = TextValue.Annotated(this)

fun StringKey.textValue(vararg args: Any): TextValue = TextValue.Res(
    value = this,
    args = args.toList(),
)

fun StringKey.textValue(quantity: Int, vararg args: Any): TextValue = TextValue.Plural(
    value = this,
    quantity = quantity,
    args = args.toList().ifEmpty { listOf(quantity) },
)