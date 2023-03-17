package io.snaps.featurecreate.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import io.snaps.baseplayer.ui.ReelPlayer
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.status.FullScreenLoaderUi
import io.snaps.featurecreate.ScreenNavigator
import io.snaps.featurecreate.viewmodel.PreviewViewModel

@Composable
fun PreviewScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<PreviewViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    viewModel.command.collectAsCommand {
        when (it) {
            PreviewViewModel.Command.CloseScreen -> router.back()
        }
    }

    PreviewScreen(
        uiState = uiState,
        onBackClicked = router::back,
        onProceedClicked = viewModel::onProceedClicked,
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun PreviewScreen(
    uiState: PreviewViewModel.UiState,
    onBackClicked: () -> Boolean,
    onProceedClicked: () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        ReelPlayer(
            videoClipUri = uiState.uri,
            shouldPlay = true,
            isMuted = false,
            isScrolling = false,
            onMuted = {},
        )
        Row(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            SimpleButtonActionM(onClick = { onBackClicked() }) {
                SimpleButtonContent(text = StringKey.PreviewVideoActionDiscard.textValue())
            }
            SimpleButtonActionM(onClick = onProceedClicked) {
                SimpleButtonContent(text = StringKey.PreviewVideoActionProceed.textValue())
            }
        }
    }
    FullScreenLoaderUi(isLoading = uiState.isLoading)
}