package io.snaps.basewallet.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.CryptoAddress
import io.snaps.corecommon.strings.StringKey
import io.snaps.corecommon.strings.addressEllipsized
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.bottomsheetdialog.SimpleBottomDialogUI
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonGreyM
import io.snaps.coreuitheme.compose.AppTheme

// todo localize common strings
@Composable
fun TopUpDialog(
    title: TextValue,
    qr: Bitmap?,
    address: CryptoAddress,
    message: TextValue? = null,
    onAddressCopyClicked: () -> Unit,
) {
    SimpleBottomDialogUI(header = title) {
        item {
            qr?.let {
                Image(
                    modifier = Modifier.size(164.dp),
                    bitmap = it.asImageBitmap(),
                    contentScale = ContentScale.FillWidth,
                    contentDescription = null,
                )
            }
            SimpleButtonGreyM(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                onClick = onAddressCopyClicked,
            ) {
                SimpleButtonContent(
                    text = address.addressEllipsized.textValue(),
                    iconRight = AppTheme.specificIcons.copy,
                )
            }
            message?.let {
                Text(
                    text = it.get(),
                    style = AppTheme.specificTypography.bodySmall,
                    color = AppTheme.specificColorScheme.textSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 24.dp),
                    textAlign = TextAlign.Center,
                )
            }
            Text(
                text = StringKey.WalletMessageTopUp.textValue().get(),
                style = AppTheme.specificTypography.bodySmall,
                color = AppTheme.specificColorScheme.textSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 24.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}