package io.snaps.coreuicompose.uikit.bottomsheetdialog

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.IconValue
import io.snaps.corecommon.container.TextValue
import io.snaps.coreuicompose.tools.doOnClick
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuitheme.compose.AppTheme

enum class ActionColor {
    Neutral, Negative
}
data class ActionData(
    val text: TextValue,
    val icon: IconValue? = null,
    val color: ActionColor = ActionColor.Neutral,
    val onClick: () -> Unit,
)

@Composable
fun ActionsBottomDialog(
    title: TextValue,
    actions: List<ActionData>,
) {
    SimpleBottomDialogUI(header = title) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(actions) { item ->
            val color = when (item.color) {
                ActionColor.Negative -> AppTheme.specificColorScheme.uiSystemRed
                ActionColor.Neutral -> AppTheme.specificColorScheme.textPrimary
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .doOnClick(onClick = item.onClick)
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                item.icon?.let {
                    Icon(
                        painter = it.get(),
                        tint = color,
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .padding(end = 8.dp),
                    )
                }
                Text(
                    text = item.text.get(),
                    color = color,
                    style = AppTheme.specificTypography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}