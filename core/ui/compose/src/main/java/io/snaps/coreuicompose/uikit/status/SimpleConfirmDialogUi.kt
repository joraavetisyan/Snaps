package io.snaps.coreuicompose.uikit.status

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.snaps.corecommon.container.TextValue
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonInlineM
import io.snaps.coreuitheme.compose.AppTheme

@Composable
fun SimpleConfirmDialogUi(
    title: TextValue,
    text: TextValue,
    confirmButtonText: TextValue,
    dismissButtonText: TextValue,
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = title.get(),
                modifier = Modifier.fillMaxWidth(),
                style = AppTheme.specificTypography.titleLarge,
                color = AppTheme.specificColorScheme.textPrimary,
            )
        },
        text = {
            Text(
                text = text.get(),
                modifier = Modifier.fillMaxWidth(),
                style = AppTheme.specificTypography.bodyMedium,
                color = AppTheme.specificColorScheme.textPrimary,
            )
        },
        confirmButton = {
            SimpleButtonInlineM(onClick = onConfirmRequest) {
                SimpleButtonContent(confirmButtonText)
            }
        },
        dismissButton = {
            SimpleButtonInlineM(onClick = onDismissRequest) {
                SimpleButtonContent(dismissButtonText)
            }
        },
    )
}