package io.snaps.featureprofile.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonGreyM
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featureprofile.domain.SubModel

@Composable
fun ConfirmUnsubscribeDialog(
    data: SubModel,
    onDismissRequest: () -> Unit,
    onUnsubscribeClicked: (SubModel) -> Unit,
) {
    val backgroundColor = AppTheme.specificColorScheme.grey
    Dialog(
        onDismissRequest = onDismissRequest,
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
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                data.image?.let {
                    Image(
                        painter = data.image.get(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape),
                    )
                }
                Text(
                    text = StringKey.ConfirmUnsubscribeDialogMessage.textValue(data.name).get(),
                    style = AppTheme.specificTypography.titleSmall,
                    color = AppTheme.specificColorScheme.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 16.dp),
                )
                SimpleButtonActionM(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp, shape = CircleShape),
                    onClick = { onUnsubscribeClicked(data) },
                ) {
                    SimpleButtonContent(
                        text = StringKey.ConfirmUnsubscribeDialogActionUnsubscribe.textValue(),
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                SimpleButtonGreyM(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = onDismissRequest,
                ) {
                    SimpleButtonContent(
                        text = StringKey.ConfirmUnsubscribeDialogActionCancel.textValue(),
                    )
                }
            }
        }
    }
}