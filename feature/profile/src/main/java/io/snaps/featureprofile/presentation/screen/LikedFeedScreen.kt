package io.snaps.featureprofile.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.basefeed.ui.VideoClipScreen
import io.snaps.corecommon.model.Uuid
import io.snaps.featureprofile.ScreenNavigator
import io.snaps.featureprofile.presentation.viewmodel.UserLikedVideoFeedViewModel

@Composable
fun LikedFeedScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<UserLikedVideoFeedViewModel>()

    LikedFeedScreen(
        viewModel = viewModel,
        onAuthorClicked = router::toProfileScreen,
    )
}

@Composable
private fun LikedFeedScreen(
    viewModel: UserLikedVideoFeedViewModel,
    onAuthorClicked: (Uuid) -> Unit,
) {
    VideoClipScreen(
        viewModel = viewModel,
        onAuthorClicked = onAuthorClicked,
    )
}