package io.snaps.coreuicompose.uikit.other

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.TextValue
import io.snaps.coreuicompose.tools.addIf
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
    Card(
        modifier = modifier
            .padding(12.dp)
            .shadow(elevation = 16.dp, shape = MainHeaderElementShape)
            .background(
                color = AppTheme.specificColorScheme.uiContentBg,
                shape = MainHeaderElementShape,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
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

    val backgroundColor = if (isSelected) {
        AppTheme.specificColorScheme.uiAccent.copy(alpha = 0.2f)
    } else Color.Transparent

    Text(
        text = text.get(),
        color = tabTextColor,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .weight(1f)
            .addIf(isSelected) {
                border(
                    width = 1.dp,
                    color = AppTheme.specificColorScheme.uiAccent,
                    shape = CircleShape
                )
            }
            .background(backgroundColor, shape = CircleShape)
            .defaultTileRipple(
                shape = CircleShape,
                onClick = onClick,
            )
            .padding(vertical = 4.dp),
    )
}