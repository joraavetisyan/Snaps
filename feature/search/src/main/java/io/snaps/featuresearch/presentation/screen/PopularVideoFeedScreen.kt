package io.snaps.featuresearch.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.basefeed.ui.VideoClipScreen
import io.snaps.corecommon.model.Uuid
import io.snaps.featuresearch.ScreenNavigator
import io.snaps.featuresearch.presentation.viewmodel.PopularVideoFeedViewModel

@Composable
fun PopularVideoFeedScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<PopularVideoFeedViewModel>()

    PopularVideoFeedScreen(
        viewModel = viewModel,
        onAuthorClicked = { router.toProfileScreen(it) },
    )
}

@Composable
private fun PopularVideoFeedScreen(
    viewModel: PopularVideoFeedViewModel,
    onAuthorClicked: (Uuid) -> Unit,
) {
    VideoClipScreen(
        viewModel = viewModel,
        onAuthorClicked = onAuthorClicked,
    )
}