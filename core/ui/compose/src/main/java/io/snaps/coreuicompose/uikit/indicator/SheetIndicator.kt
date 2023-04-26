package io.snaps.coreuicompose.uikit.indicator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.snaps.coreuitheme.compose.AppTheme

@Composable
fun SheetIndicator() {
    Box(
        modifier = Modifier
            .width(32.dp)
            .height(4.dp)
            .background(AppTheme.specificColorScheme.textPrimary, RoundedCornerShape(2.dp)),
    )
}