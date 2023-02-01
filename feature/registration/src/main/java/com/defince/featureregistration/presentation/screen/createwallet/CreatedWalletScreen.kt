package com.defince.featureregistration.presentation.screen.createwallet

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.defince.corecommon.R
import com.defince.corecommon.container.ImageValue
import com.defince.corecommon.container.textValue
import com.defince.corecommon.strings.StringKey
import com.defince.coreuicompose.uikit.duplicate.SimpleTopAppBar
import com.defince.coreuicompose.uikit.status.FullScreenMessage
import com.defince.coreuicompose.uikit.status.FullScreenMessageUi
import com.defince.coreuitheme.compose.AppTheme
import com.defince.featureregistration.presentation.ScreenNavigator


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatedWalletScreen(
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