package io.snaps.coreuicompose.uikit.other

import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun Progress(
    modifier: Modifier,
    @FloatRange(from = 0.0, to = 1.0) progress: Float,
    isDashed: Boolean = true,
    backColor: Color = Color(0xFFF2F4FE),
    fillColor: Color = Color(0xFFDDE3FF),
    dashColor: Color = Color(0xFFD1D9FF),
    height: Dp = 16.dp,
    cornerSize: Dp = height,
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .background(backColor, CircleShape)
            .clip(CircleShape),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0.0f, 1.0f))
                .align(Alignment.CenterStart)
                .clip(RoundedCornerShape(cornerSize)),
        ) {
            // Background
            drawRect(
                color = fillColor,
                topLeft = Offset(x = 0f, y = 0f),
                size = Size(width = size.width, height = size.height),
            )
            if (isDashed) {
                val angleDegrees = 30f
                val stepPx = height.toPx()
                val stepsCount = (size.width / stepPx).roundToInt()
                val actualStepWidth = size.width / stepsCount
                val dotSize = Size(width = actualStepWidth / 2, height = size.height * 2)
                for (i in -1..stepsCount) {
                    val rect = Rect(
                        offset = Offset(
                            x = i * actualStepWidth,
                            y = (size.height - dotSize.height) / 2,
                        ),
                        size = dotSize,
                    )
                    rotate(angleDegrees, pivot = rect.center) {
                        drawRect(
                            color = dashColor,
                            topLeft = rect.topLeft,
                            size = rect.size,
                        )
                    }
                }
            }
        }
    }
}