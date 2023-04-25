@file:Suppress("DEPRECATION")

package io.snaps.corecommon.container

import androidx.compose.ui.text.AnnotatedString
import io.snaps.corecommon.strings.StringKey

private const val msg = "Constructor usage is deprecated, use extension function instead"
private const val imp = "io.snaps.corecommon.container.textValue"

sealed class TextValue {

    data class Simple @Deprecated(
        message = msg,
        replaceWith = ReplaceWith("value.textValue()", imp)
    ) constructor(
        val value: String,
    ) : TextValue()

    data class Annotated @Deprecated(
        message = msg,
        replaceWith = ReplaceWith("value.textValue()", imp)
    ) constructor(
        val value: AnnotatedString,
    ) : TextValue()

    data class Res @Deprecated(
        message = msg,
        replaceWith = ReplaceWith("value.textValue(args = args)", imp)
    ) constructor(
        val value: StringKey,
        val args: List<Any> = emptyList(),
    ) : TextValue()

    data class Plural @Deprecated(
        message = msg,
        replaceWith = ReplaceWith("value.textValue(quantity = quantity, args = args)", imp)
    ) constructor(
        val value: StringKey,
        val quantity: Int,
        val args: List<Any> = listOf(quantity),
    ) : TextValue()

    companion object {

        val empty get() = "".textValue()
    }
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