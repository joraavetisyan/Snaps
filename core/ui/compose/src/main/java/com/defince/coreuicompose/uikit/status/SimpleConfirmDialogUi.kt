package com.defince.coreuicompose.uikit.status

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.defince.corecommon.container.TextValue
import com.defince.coreuicompose.tools.get
import com.defince.coreuicompose.uikit.button.SimpleButtonContent
import com.defince.coreuicompose.uikit.button.SimpleButtonInlineM
import com.defince.coreuitheme.compose.AppTheme

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
                color = AppTheme.specificColorScheme.symbolPrimary,
            )
        },
        text = {
            Text(
                text = text.get(),
                modifier = Modifier.fillMaxWidth(),
                style = AppTheme.specificTypography.bodyMedium,
                color = AppTheme.specificColorScheme.symbolPrimary,
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