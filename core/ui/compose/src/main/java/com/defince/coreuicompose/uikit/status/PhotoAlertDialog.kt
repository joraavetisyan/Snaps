package com.defince.coreuicompose.uikit.status

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.defince.corecommon.strings.StringKey
import com.defince.coreuitheme.compose.AppTheme
import com.defince.coreuitheme.compose.LocalStringHolder

@Composable
fun PhotoAlertDialog(
    onDismissRequest: () -> Unit,
    onTakePhotoClicked: () -> Unit,
    onPickPhotoClicked: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = LocalStringHolder.current(StringKey.PhotoDialogTitle),
                style = AppTheme.specificTypography.bodyLarge,
                textAlign = TextAlign.Center,
                color = AppTheme.specificColorScheme.textPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
            )
        },
        shape = RoundedCornerShape(4.dp),
        backgroundColor = AppTheme.specificColorScheme.white,
        buttons = {
            Column(
                modifier = Modifier.padding(bottom = 12.dp),
            ) {
                Text(
                    text = LocalStringHolder.current(StringKey.PhotoDialogActionPickPhoto),
                    style = AppTheme.specificTypography.bodyMedium,
                    color = AppTheme.specificColorScheme.uiAccent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onPickPhotoClicked)
                        .padding(vertical = 12.dp, horizontal = 24.dp)
                )
                Text(
                    text = LocalStringHolder.current(StringKey.PhotoDialogActionTakePhoto),
                    style = AppTheme.specificTypography.bodyMedium,
                    color = AppTheme.specificColorScheme.uiAccent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onTakePhotoClicked)
                        .padding(vertical = 12.dp, horizontal = 24.dp),
                )
                Text(
                    text = LocalStringHolder.current(StringKey.PhotoDialogActionCancel),
                    style = AppTheme.specificTypography.bodyMedium,
                    color = AppTheme.specificColorScheme.uiAccent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onDismissRequest)
                        .padding(vertical = 12.dp, horizontal = 24.dp),
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
    PhotoAlertDialog(
        onDismissRequest = {},
        onTakePhotoClicked = {},
        onPickPhotoClicked = {},
    )
}

