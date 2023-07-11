package io.snaps.coreuicompose.uikit.duplicate

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.IconValue
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuitheme.compose.AppTheme

typealias OnBackIconClick = () -> Boolean

data class ActionIconData(
    val icon: IconValue,
    val color: Color? = null,
    val onClick: () -> Unit,
)

@Composable
fun TopAppBarActionIcon(
    data: ActionIconData,
) {
    Icon(
        painter = data.icon.get(),
        contentDescription = null,
        tint = data.color ?: LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
        modifier = Modifier
            .clip(CircleShape)
            .clickable(enabled = true, onClick = data.onClick)
            .padding(6.dp),
    )
}

@Composable
fun CollapsedTopAppBarActionIcon(
    data: ActionIconData,
) {
    Icon(
        painter = data.icon.get(),
        contentDescription = null,
        tint = AppTheme.specificColorScheme.textPrimary,
        modifier = Modifier
            .clip(CircleShape)
            .clickable(enabled = true, onClick = data.onClick)
            .padding(6.dp),
    )
}

@Composable
fun TopAppBarNavIcon(iconValue: IconValue, onClick: () -> Unit) {
    Icon(
        painter = iconValue.get(),
        tint = AppTheme.specificColorScheme.textPrimary,
        contentDescription = "navigation icon",
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(8.dp),
    )
}