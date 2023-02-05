package io.snaps.coreuicompose.uikit.status

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonGreyM
import io.snaps.coreuitheme.compose.AppTheme

data class ButtonData(
    val text: TextValue,
    val onClick: () -> Unit,
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SimpleAlertDialogUi(
    title: TextValue,
    message: TextValue,
    primaryButton: ButtonData? = null,
    secondaryButton: ButtonData? = null,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val backgroundColor = AppTheme.specificColorScheme.grey
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = AppTheme.shapes.medium,
            color = backgroundColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
            ) {
                Text(
                    text = title.get(),
                    style = AppTheme.specificTypography.headlineSmall,
                    color = AppTheme.specificColorScheme.textPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = message.get(),
                    style = AppTheme.specificTypography.titleSmall,
                    color = AppTheme.specificColorScheme.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 16.dp),
                )
                content()
                Spacer(modifier = Modifier.height(16.dp))
                primaryButton?.let {
                    SimpleButtonActionM(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(16.dp, shape = CircleShape),
                        onClick = it.onClick,
                    ) {
                        SimpleButtonContent(text = it.text)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                secondaryButton?.let {
                    SimpleButtonGreyM(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(16.dp, shape = CircleShape),
                        onClick = it.onClick,
                    ) {
                        SimpleButtonContent(text = it.text)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
@Preview
fun Test() {
    SimpleAlertDialogUi(
        title = "Title".textValue(),
        message = "vjiofjgoi ioghjtohlgjkh fhguygf".textValue(),
        primaryButton = ButtonData(
            text = "Primary Button".textValue(),
            onClick = {},
        ),
        secondaryButton = ButtonData(
            text = "Secondary Button".textValue(),
            onClick = {},
        ),
        onDismissRequest = {},
    ) {
        Text(
            text = "text",
            style = AppTheme.specificTypography.titleSmall,
            color = AppTheme.specificColorScheme.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}