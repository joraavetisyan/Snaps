package io.snaps.coreuicompose.tools

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

fun Offset.toIntOffset() = IntOffset(
    x = x.toInt(),
    y = y.toInt(),
)

fun LayoutCoordinates?.intOffsetInParent() = this?.positionInParent()?.toIntOffset() ?: IntOffset.Zero

fun IntSize.toDpSize(density: Density) = with(density) { DpSize(width = width.toDp(), height = height.toDp()) }

fun LayoutCoordinates?.dpSize(density: Density) = this?.size?.toDpSize(density) ?: DpSize.Unspecified

fun IntOffset.add(density: Density, x: Dp = 0.dp, y: Dp = 0.dp) = IntOffset(
    x = this.x + with(density) { x.toPx().toInt() },
    y = this.y + with(density) { y.toPx().toInt() },
)

@Composable
fun Dp.toPx() = with(LocalDensity.current) { this@toPx.toPx() }

@Composable
fun TextUnit.toPx() = with(LocalDensity.current) { this@toPx.toPx() }