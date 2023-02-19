package io.snaps.featurefeed.presentation.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.basefeed.ui.VideoClipScreen
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.ui.MainHeader
import io.snaps.baseprofile.ui.MainHeaderState
import io.snaps.corecommon.model.Uuid
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.featurefeed.ScreenNavigator
import io.snaps.featurefeed.presentation.viewmodel.MainVideoFeedViewModel

@Composable
fun MainVideoFeedScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<MainVideoFeedViewModel>()
    val mainHeaderUiState by viewModel.headerUiState.collectAsState()

    viewModel.headerCommand.collectAsCommand {
        when (it) {
            MainHeaderHandler.Command.OpenProfileScreen -> router.toProfileScreen()
            MainHeaderHandler.Command.OpenWalletScreen -> router.toWalletScreen()
        }
    }

    MainVideoFeedScreen(
        viewModel = viewModel,
        onAuthorClicked = { router.toProfileScreen(it) },
        mainHeaderState = mainHeaderUiState.value,
    )
}

@Composable
private fun MainVideoFeedScreen(
    viewModel: MainVideoFeedViewModel,
    onAuthorClicked: (Uuid) -> Unit,
    mainHeaderState: MainHeaderState,
) {
    VideoClipScreen(
        viewModel = viewModel,
        onAuthorClicked = onAuthorClicked,
    ) {
        MainHeader(
            state = mainHeaderState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(it)
                .padding(top = 16.dp),
        )
    }
}