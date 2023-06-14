package io.snaps.featurewallet.screen

import androidx.compose.runtime.Composable
import io.snaps.basewallet.ui.TransferTokensDialogHandler
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.corecommon.R
import io.snaps.corecommon.container.imageValue
import io.snaps.coreuicompose.uikit.bottomsheetdialog.SimpleBottomDialog

@Composable
fun TokensTransferSuccessDialog(
    dialog: TransferTokensDialogHandler.BottomDialog.TokensTransferSuccess,
    onClick: () -> Unit,
) {
    SimpleBottomDialog(
        image = R.drawable.img_guy_hands_up.imageValue(),
        title = StringKey.WithdrawDialogWithdrawSuccessTitle.textValue(),
        text = StringKey.WithdrawDialogWithdrawSuccessMessage.textValue(
            dialog.sent?.getFormatted().orEmpty(),
            dialog.to.orEmpty()
        ),
        buttonText = StringKey.WithdrawDialogWithdrawSuccessAction.textValue(),
        onClick = onClick,
    )
}