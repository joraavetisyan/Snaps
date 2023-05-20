@file:Suppress("DEPRECATION")

package io.snaps.corecommon.container

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

private const val message = "Constructor usage is deprecated, use extension function instead"
private const val import = "io.snaps.corecommon.container.iconValue"

sealed class IconValue(val value: Any?) {

    class ResVector @Deprecated(
        message = message,
        replaceWith = ReplaceWith("value.iconValue()", import),
    ) constructor(
        @DrawableRes value: Int,
    ) : IconValue(value)

    class Vector @Deprecated(
        message = message,
        replaceWith = ReplaceWith("value.iconValue()", import),
    ) constructor(
        value: ImageVector,
    ) : IconValue(value)

    fun toImageValue() = when (this) {
        is ResVector -> ImageValue.ResVector(value as Int)
        is Vector -> (value as ImageVector).imageValue()
    }
}

fun @receiver:DrawableRes Int.iconValue(): IconValue = IconValue.ResVector(this)

fun ImageVector.iconValue(): IconValue = IconValue.Vector(this)