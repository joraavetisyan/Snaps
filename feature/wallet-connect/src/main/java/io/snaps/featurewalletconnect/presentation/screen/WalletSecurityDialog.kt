package io.snaps.featurewalletconnect.presentation.screen

import androidx.compose.runtime.Composable
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.uikit.dialog.SimpleAlertDialogUi
import io.snaps.featurewalletconnect.presentation.viewmodel.WalletSecurityErrorHandler

@Composable
fun WalletSecurityDialog(
    uiState: WalletSecurityErrorHandler.UiState,
    onDialogDismissRequested: () -> Unit,
) {
    when (uiState.dialog) {
        WalletSecurityErrorHandler.Dialog.ScreenLockNotSet -> SimpleAlertDialogUi(
            title = StringKey.WalletSecurityDialogTitle.textValue(),
            text = StringKey.WalletSecurityDialogMessageScreenLockNotSet.textValue(),
            buttonText = StringKey.ActionClose.textValue(),
            onClickRequest = onDialogDismissRequested,
        )
        WalletSecurityErrorHandler.Dialog.UserNotAuthenticatedRecently -> SimpleAlertDialogUi(
            title = StringKey.WalletSecurityDialogTitle.textValue(),
            text = StringKey.WalletSecurityDialogMessageUserNotAuthenticatedRecently.textValue(),
            buttonText = StringKey.ActionClose.textValue(),
            onClickRequest = onDialogDismissRequested,
        )
        WalletSecurityErrorHandler.Dialog.DeviceNotSecured -> SimpleAlertDialogUi(
            title = StringKey.WalletSecurityDialogTitle.textValue(),
            text = StringKey.WalletSecurityDialogMessage.textValue(),
            buttonText = StringKey.ActionClose.textValue(),
            onClickRequest = onDialogDismissRequested,
        )
        null -> Unit
    }
}