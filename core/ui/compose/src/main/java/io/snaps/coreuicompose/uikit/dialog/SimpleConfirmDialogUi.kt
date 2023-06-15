package io.snaps.coreuicompose.uikit.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonInlineM
import io.snaps.coreuitheme.compose.AppTheme

@Composable
fun SimpleConfirmDialogUi(
    title: TextValue,
    text: TextValue? = null,
    confirmButtonText: TextValue = StringKey.ActionConfirm.textValue(),
    dismissButtonText: TextValue = StringKey.ActionCancel.textValue(),
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = title.get(),
                modifier = Modifier.fillMaxWidth(),
                style = AppTheme.specificTypography.titleMedium,
                color = AppTheme.specificColorScheme.textPrimary,
            )
        },
        text = {
            text?.let {
                Text(
                    text = it.get(),
                    modifier = Modifier.fillMaxWidth(),
                    style = AppTheme.specificTypography.bodyMedium,
                    color = AppTheme.specificColorScheme.textPrimary,
                )
            }
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