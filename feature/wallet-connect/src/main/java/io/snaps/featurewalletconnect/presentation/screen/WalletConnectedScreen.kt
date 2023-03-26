package io.snaps.featurewalletconnect.presentation.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.uikit.status.FullScreenLoaderUi
import io.snaps.coreuicompose.uikit.status.FullScreenMessage
import io.snaps.coreuicompose.uikit.status.FullScreenMessageUi
import io.snaps.featurewalletconnect.ScreenNavigator
import io.snaps.featurewalletconnect.presentation.viewmodel.WalletConnectedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletConnectedScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<WalletConnectedViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold {
        FullScreenMessageUi(
            modifier = Modifier.padding(it),
            data = FullScreenMessage(
                icon = ImageValue.ResImage(R.drawable.img_wallet),
                title = StringKey.CreatedWalletTitle.textValue(),
                message = StringKey.CreatedWalletMessage.textValue(),
                primaryButton = FullScreenMessage.ButtonData(
                    text = StringKey.CreatedWalletAction.textValue(),
                    onClick = viewModel::onContinueButtonClicked,
                ),
            ),
        )
    }
    FullScreenLoaderUi(isLoading = uiState.isLoading)
}