package io.snaps.coreuicompose.uikit.other

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.TextValue
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.MainHeaderElementShape

@Composable
fun TitleSlider(
    modifier: Modifier = Modifier,
    selectedItemIndex: Int,
    items: List<TextValue>,
    onClick: (index: Int) -> Unit,
) {
    var tabWidth by remember { mutableStateOf(0.dp) }

    val indicatorOffset: Dp by animateDpAsState(
        targetValue = tabWidth * selectedItemIndex,
        animationSpec = tween(easing = LinearEasing),
    )

    val localDensity = LocalDensity.current

    Box(
        modifier = modifier
            .padding(12.dp)
            .shadow(elevation = 16.dp, shape = MainHeaderElementShape)
            .background(
                color = AppTheme.specificColorScheme.uiContentBg,
                shape = MainHeaderElementShape,
            )
            .height(intrinsicSize = IntrinsicSize.Min)
    ) {
        TabIndicator(
            indicatorWidth = tabWidth,
            indicatorOffset = indicatorOffset,
            indicatorColor = AppTheme.specificColorScheme.uiAccent.copy(alpha = 0.2f),
        )
        Row(
            modifier = Modifier
                .onGloballyPositioned {
                    tabWidth = with(localDensity) { it.size.width.toDp() / 2 }
                }
                .padding(vertical = 2.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { index, item ->
                TabItem(
                    isSelected = index == selectedItemIndex,
                    text = item,
                    onClick = { onClick(index) },
                )
            }
        }
    }
}

@Composable
private fun TabIndicator(
    indicatorWidth: Dp,
    indicatorOffset: Dp,
    indicatorColor: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(width = indicatorWidth)
            .offset(x = indicatorOffset)
            .padding(vertical = 2.dp, horizontal = 4.dp)
            .border(
                width = 1.dp,
                color = AppTheme.specificColorScheme.uiAccent,
                shape = CircleShape,
            )
            .clip(shape = CircleShape)
            .background(color = indicatorColor),
    )
}

@Composable
private fun RowScope.TabItem(
    isSelected: Boolean,
    text: TextValue,
    onClick: () -> Unit,
) {
    val tabTextColor: Color by animateColorAsState(
        targetValue = if (isSelected) {
            AppTheme.specificColorScheme.uiAccent
        } else AppTheme.specificColorScheme.textSecondary,
        animationSpec = tween(easing = LinearEasing),
    )

    Text(
        text = text.get(),
        color = tabTextColor,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .weight(1f)
            .clip(shape = CircleShape)
            .defaultTileRipple(
                shape = CircleShape,
                onClick = onClick,
            )
            .padding(vertical = 4.dp, horizontal = 16.dp),
    )
}