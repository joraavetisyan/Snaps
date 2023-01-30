package com.defince.coreuicompose.uikit.status

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.defince.corecommon.container.TextValue
import com.defince.coreuicompose.tools.get
import com.defince.coreuicompose.uikit.button.SimpleButtonContent
import com.defince.coreuicompose.uikit.button.SimpleButtonInlineS
import com.defince.coreuitheme.compose.AppTheme

@Composable
fun SimpleAlertDialogUi(
    text: TextValue,
    buttonText: TextValue,
    onClickRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onClickRequest,
        text = {
            Text(
                text = text.get(),
                modifier = Modifier.fillMaxWidth(),
                style = AppTheme.specificTypography.bodyLarge,
                color = AppTheme.specificColorScheme.textPrimary
            )
        },
        dismissButton = {
            SimpleButtonInlineS(onClick = onClickRequest) {
                SimpleButtonContent(buttonText)
            }
        },
        confirmButton = {},
    )
}