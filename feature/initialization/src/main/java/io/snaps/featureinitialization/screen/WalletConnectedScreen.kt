package io.snaps.featureinitialization.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuicompose.uikit.status.FullScreenMessage
import io.snaps.coreuicompose.uikit.status.FullScreenMessageUi
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featureinitialization.ScreenNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletConnectedScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = null,
                scrollBehavior = scrollBehavior,
                navigationIcon = AppTheme.specificIcons.back to router::back,
            )
        },
    ) {
        FullScreenMessageUi(
            modifier = Modifier.padding(it),
            data = FullScreenMessage(
                icon = ImageValue.ResImage(R.drawable.img_wallet),
                title = StringKey.CreatedWalletTitle.textValue(),
                message = StringKey.CreatedWalletMessage.textValue(),
                primaryButton = FullScreenMessage.ButtonData(
                    text = StringKey.CreatedWalletAction.textValue(),
                    onClick = { /*todo*/ },
                ),
            ),
        )
    }
}