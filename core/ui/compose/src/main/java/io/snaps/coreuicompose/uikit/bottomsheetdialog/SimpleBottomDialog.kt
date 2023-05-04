package io.snaps.coreuicompose.uikit.bottomsheetdialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.IconValue
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionL
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuitheme.compose.AppTheme

@Composable
fun SimpleBottomDialog(
    image: ImageValue? = null,
    title: TextValue? = null,
    text: TextValue? = null,
    buttonText: TextValue? = null,
    buttonIcon: IconValue? = null,
    onClick: (() -> Unit)? = null,
) {
    SimpleBottomDialogUI {
        item {
            image?.let {
                Image(
                    painter = image.get(),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp, 120.dp),
                )
            }
            title?.let {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = title.get(),
                    style = AppTheme.specificTypography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
            }
            text?.let {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = text.get(),
                    style = AppTheme.specificTypography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
            }
            onClick?.let {
                Spacer(modifier = Modifier.height(24.dp))
                SimpleButtonActionL(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    SimpleButtonContent(text = buttonText, iconLeft = buttonIcon)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}