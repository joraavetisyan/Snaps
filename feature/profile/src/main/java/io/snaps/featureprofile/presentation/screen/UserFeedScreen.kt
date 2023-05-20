package io.snaps.featureprofile.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.basefeed.ui.VideoClipScreen
import io.snaps.corecommon.model.Uuid
import io.snaps.featureprofile.ScreenNavigator
import io.snaps.featureprofile.presentation.viewmodel.UserVideoFeedViewModel

@Composable
fun UserFeedScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<UserVideoFeedViewModel>()

    UserFeedScreen(
        viewModel = viewModel,
        onAuthorClicked = { router.back() },
        onCloseScreen = { router.back() }
    )
}

@Composable
private fun UserFeedScreen(
    viewModel: UserVideoFeedViewModel,
    onAuthorClicked: (Uuid) -> Unit,
    onCloseScreen: () -> Unit,
) {
    VideoClipScreen(
        viewModel = viewModel,
        onAuthorClicked = onAuthorClicked,
        onCloseScreen = onCloseScreen,
    )
}