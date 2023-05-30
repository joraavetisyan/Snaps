package io.snaps.featurewalletconnect.presentation.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.TextValue
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.error
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuitheme.compose.AppTheme

data class SelectorTileData(
    val text: TextValue,
    val status: SelectorTileStatus,
    val clickListener: () -> Unit,
    val onAnimationFinished: () -> Unit,
)

enum class SelectorTileStatus {
    Selected, Error, Default,
}

private val offsetAnimSpec = tween<Float>(30)

@Composable
fun SelectorTile(
    data: SelectorTileData,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val textColor = when (data.status) {
        SelectorTileStatus.Selected -> AppTheme.specificColorScheme.uiAccent
        SelectorTileStatus.Default -> AppTheme.specificColorScheme.textPrimary
        SelectorTileStatus.Error -> AppTheme.specificColorScheme.uiSystemRed
    }

    LaunchedEffect(data.status) {
        if (data.status == SelectorTileStatus.Error) {
            hapticFeedback.error()
        }
    }

    val offsetX = remember { Animatable(0f) }
    LaunchedEffect(data.status) {
        if (data.status == SelectorTileStatus.Error) {
            var offset = 5f
            while (offset >= 0) {
                offsetX.animateTo(targetValue = -offset, animationSpec = offsetAnimSpec)
                offsetX.animateTo(targetValue = offset--, animationSpec = offsetAnimSpec)
            }
            data.onAnimationFinished()
        }
    }

    Box(
        modifier = modifier
            .offset(x = offsetX.value.dp)
            .background(AppTheme.specificColorScheme.lightGrey, CircleShape)
            .defaultTileRipple(shape = CircleShape) { data.clickListener() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
    ) {
        Text(
            modifier = modifier.fillMaxWidth(),
            text = data.text.get(),
            color = textColor,
            style = AppTheme.specificTypography.titleSmall,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}