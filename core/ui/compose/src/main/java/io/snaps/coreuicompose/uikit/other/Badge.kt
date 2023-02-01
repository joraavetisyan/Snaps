package io.snaps.coreuicompose.uikit.other

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.snaps.coreuitheme.compose.AppTheme

@Composable
fun Badge(
    modifier: Modifier,
) {
    Box(
        modifier = modifier
            .size(6.dp)
            .background(AppTheme.specificColorScheme.actionBase, CircleShape),
    )
}