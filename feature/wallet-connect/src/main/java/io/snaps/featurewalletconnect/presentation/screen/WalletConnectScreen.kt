package io.snaps.featurewalletconnect.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import io.snaps.corecommon.R
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.uikit.status.FullScreenMessage
import io.snaps.coreuicompose.uikit.status.FullScreenMessageUi
import io.snaps.featurewalletconnect.ScreenNavigator

@Composable
fun WalletConnectScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }

    FullScreenMessageUi(
        data = FullScreenMessage(
            icon = R.drawable.img_wallet.imageValue(),
            title = StringKey.ConnectWalletTitle.textValue(),
            message = StringKey.ConnectWalletMessage.textValue(),
            primaryButton = FullScreenMessage.ButtonData(
                text = StringKey.ConnectWalletActionCreate.textValue(),
                onClick = router::toWalletCreateScreen,
            ),
            secondaryButton = FullScreenMessage.ButtonData(
                text = StringKey.ConnectWalletActionImport.textValue(),
                onClick = router::toWalletImportScreen,
            ),
        )
    )
}