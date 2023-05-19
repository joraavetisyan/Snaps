@file:Suppress("DEPRECATION")

package io.snaps.corecommon.container

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector
import io.snaps.corecommon.model.FullUrl

private const val message = "Constructor usage is deprecated, use extension function instead"
private const val import = "io.snaps.corecommon.container.imageValue"

sealed class ImageValue(val value: Any?) {

    class Url @Deprecated(
        message = message,
        replaceWith = ReplaceWith("value.imageValue()", import),
    ) constructor(
        value: FullUrl,
    ) : ImageValue(value)

    class Image @Deprecated(
        message = message,
        replaceWith = ReplaceWith("value.imageValue()", import),
    ) constructor(
        value: Bitmap,
    ) : ImageValue(value)

    class ResImage @Deprecated(
        message = message,
        replaceWith = ReplaceWith("value.imageValue()", import),
    ) constructor(
        @DrawableRes value: Int,
    ) : ImageValue(value)

    class ResVector(
        @DrawableRes value: Int,
    ) : ImageValue(value)

    class Vector @Deprecated(
        message = message,
        replaceWith = ReplaceWith("value.imageValue()", import),
    ) constructor(
        value: ImageVector,
    ) : ImageValue(value)
}

fun FullUrl.imageValue(): ImageValue = ImageValue.Url(this)

fun Bitmap.imageValue(): ImageValue = ImageValue.Image(this)

fun @receiver:DrawableRes Int.imageValue(): ImageValue = ImageValue.ResImage(this)

fun ImageVector.imageValue(): ImageValue = ImageValue.Vector(this)