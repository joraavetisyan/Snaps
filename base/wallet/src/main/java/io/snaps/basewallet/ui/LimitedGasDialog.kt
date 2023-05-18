package io.snaps.basewallet.ui

import androidx.compose.runtime.Composable
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.uikit.bottomsheetdialog.SimpleBottomDialog

@Composable
fun LimitedGasDialog(
    onRefillClick: () -> Unit,
) {
    SimpleBottomDialog(
        image = ImageValue.ResImage(R.drawable.img_guy_glad),
        title = StringKey.DialogLimitedGasTitle.textValue(),
        text = StringKey.DialogLimitedGasMessage.textValue(),
        buttonText = StringKey.DialogLimitedGasAction.textValue(),
        onClick = onRefillClick,
    )
}