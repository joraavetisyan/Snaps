package io.snaps.coreuicompose.uikit.status

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.TextValue
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuitheme.compose.AppTheme

@Composable
fun InfoBlock(
    modifier: Modifier = Modifier,
    message: TextValue
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = AppTheme.specificColorScheme.uiSystemYellow.copy(alpha = 0.3f),
                shape = AppTheme.shapes.medium,
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            painter = AppTheme.specificIcons.info.get(),
            contentDescription = null,
            tint = AppTheme.specificColorScheme.uiSystemOrange,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = message.get(),
            style = AppTheme.specificTypography.labelSmall,
            color = AppTheme.specificColorScheme.uiSystemOrange,
        )
    }
}