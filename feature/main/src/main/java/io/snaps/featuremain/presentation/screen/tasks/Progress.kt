package io.snaps.featuremain.presentation.screen.tasks

import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun Progress(
    modifier: Modifier,
    @FloatRange(from = 0.0, to = 1.0) progress: Float,
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(16.dp)
            .background(Color(0xFFF2F4FE), CircleShape),
    ) {
        Canvas(
            modifier = modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0.0f, 1.0f))
                .align(Alignment.CenterStart)
                .clip(CircleShape),
        ) {
            val step = 16.dp
            val angleDegrees = 30f
            val stepPx = step.toPx()
            val stepsCount = (size.width / stepPx).roundToInt()
            val actualStepWidth = size.width / stepsCount
            val dotSize = Size(width = actualStepWidth / 2, height = size.height * 2)
            // Background
            drawRect(
                color = Color(0xFFDDE3FF),
                topLeft = Offset(x = 0f, y = 0f),
                size = Size(width = size.width, height = size.height),
            )
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
                        color = Color(0xFFD1D9FF),
                        topLeft = rect.topLeft,
                        size = rect.size,
                    )
                }
            }
        }
    }
}