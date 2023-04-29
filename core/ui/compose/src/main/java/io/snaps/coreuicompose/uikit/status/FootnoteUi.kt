package io.snaps.coreuicompose.uikit.status

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.TextValue
import io.snaps.coreuicompose.tools.doOnClick
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuitheme.compose.AppTheme

@Composable
fun FootnoteUi(
    title: TextValue? = null,
    description: TextValue? = null,
    action: TextValue? = null,
    onClick: (() -> Unit)? = null,
    padding: Dp = 16.dp,
) {
    Column(
        modifier = Modifier.padding(padding),
    ) {
        title?.let {
            Text(
                text = title.get(),
                style = AppTheme.specificTypography.headlineMedium,
            )
        }
        description?.let {
            Text(
                text = description.get(),
                style = AppTheme.specificTypography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
        action?.let {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.doOnClick(onClick = onClick),
            ) {
                Icon(
                    painter = AppTheme.specificIcons.question.get(),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp),
                )
                Text(
                    text = action.get(),
                    style = AppTheme.specificTypography.titleSmall,
                    color = AppTheme.specificColorScheme.textLink,
                )
            }
        }
    }
}