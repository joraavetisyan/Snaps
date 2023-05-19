@file:Suppress("DEPRECATION")

package io.snaps.corecommon.container

import androidx.annotation.ColorRes
import androidx.compose.ui.graphics.Color

private const val message = "Constructor usage is deprecated, use extension function instead"
private const val import = "io.snaps.corecommon.container.colorValue"

sealed class ColorValue {

    data class Simple @Deprecated(
        message = message,
        replaceWith = ReplaceWith("value.colorValue()", import),
    ) constructor(
        val color: Color,
    ) : ColorValue()

    data class Res @Deprecated(
        message = message,
        replaceWith = ReplaceWith("value.colorValue()", import),
    ) constructor(
        @ColorRes val res: Int,
    ) : ColorValue()
}

@Throws
fun String.colorValue(): ColorValue {
    var hexCode = replace("#", "")
    if (hexCode.length == 6) {
        hexCode = "FF$hexCode"
    }
    return Color("0x$hexCode".toLong()).colorValue()
}

fun Color.colorValue(): ColorValue = ColorValue.Simple(this)

fun @receiver:ColorRes Int.colorValue(): ColorValue = ColorValue.Res(this)