package io.snaps.featureinitialization.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.TextValue
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
            repeat(4) {
                offsetX.animateTo(
                    targetValue = -5f,
                    animationSpec = tween(50),
                )
                offsetX.animateTo(
                    targetValue = 5f,
                    animationSpec = tween(50),
                )
                offsetX.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(50),
                )
            }
            data.onAnimationFinished()
        }
    }

    Box(
        modifier = modifier
            .offset(x = offsetX.value.dp)
            .shadow(12.dp, CircleShape)
            .background(AppTheme.specificColorScheme.lightGrey, CircleShape)
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .clickable { data.clickListener() },
    ) {
        Text(
            text = data.text.get(),
            color = textColor,
            style = AppTheme.specificTypography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}