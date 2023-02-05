package io.snaps.coreuicompose.uikit.other

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import io.snaps.coreuitheme.compose.AppTheme

@Composable
fun SimpleCard(
    modifier: Modifier = Modifier,
    shape: Shape = AppTheme.shapes.medium,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp, shape = shape)
            .background(
                color = AppTheme.specificColorScheme.uiSystemRed,
                shape = shape,
            ),
        content = content,
    )
}