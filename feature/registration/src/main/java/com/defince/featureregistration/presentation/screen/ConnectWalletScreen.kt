package com.defince.featureregistration.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.defince.corecommon.container.textValue
import com.defince.corecommon.R
import com.defince.corecommon.container.ImageValue
import com.defince.corecommon.strings.StringKey
import com.defince.coreuicompose.uikit.status.FullScreenMessage
import com.defince.coreuicompose.uikit.status.FullScreenMessageUi
import com.defince.featureregistration.presentation.ScreenNavigator

@Composable
fun ConnectWalletScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }

    FullScreenMessageUi(
        data = FullScreenMessage(
            icon = ImageValue.ResImage(R.drawable.img_wallet),
            title = StringKey.ConnectWalletTitle.textValue(),
            message = StringKey.ConnectWalletMessage.textValue(),
            primaryButton = FullScreenMessage.ButtonData(
                text = StringKey.ConnectWalletActionCreate.textValue(),
                onClick = router::toCreateWalletScreen,
            ),
            secondaryButton = FullScreenMessage.ButtonData(
                text = StringKey.ConnectWalletActionImport.textValue(),
                onClick = router::toWalletImportScreen,
            ),
        )
    )
}