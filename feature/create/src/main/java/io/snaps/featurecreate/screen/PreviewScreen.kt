package io.snaps.featurecreate.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.baseplayer.ui.VideoPlayer
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAll
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonInlineL
import io.snaps.coreuicompose.uikit.other.Progress
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featurecreate.ScreenNavigator
import io.snaps.featurecreate.viewmodel.PreviewViewModel
import ly.img.android.pesdk.VideoEditorSettingsList
import ly.img.android.pesdk.backend.model.EditorSDKResult
import ly.img.android.pesdk.backend.model.state.LoadSettings
import ly.img.android.pesdk.ui.activity.VideoEditorActivityResultContract

@Composable
fun PreviewScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<PreviewViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    val editorLauncher = rememberLauncherForActivityResult(
        contract = VideoEditorActivityResultContract(),
        onResult = { result ->
            when (result.resultStatus) {
                EditorSDKResult.Status.EXPORT_DONE -> result.resultUri?.path?.let(router::toUploadScreen)
                else -> Unit
            }
        }
    )

    viewModel.command.collectAsCommand {
        when (it) {
            is PreviewViewModel.Command.OpenEditorScreen -> {
                val settingsList = VideoEditorSettingsList(false)
                    .configure<LoadSettings> { loadSettings -> loadSettings.source = it.uri }
                editorLauncher.launch(settingsList)
                settingsList.release()
            }
        }
    }

    PreviewScreen(
        uiState = uiState,
        onBackClicked = router::back,
        onProgressChanged = viewModel::onProgressChanged,
        onProceedClicked = viewModel::onProceedClicked,
    )
}

@Composable
private fun PreviewScreen(
    uiState: PreviewViewModel.UiState,
    onBackClicked: () -> Unit,
    onProgressChanged: (Float) -> Unit,
    onProceedClicked: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.5f)))
                )
        )
        Box(
            Modifier
                .fillMaxWidth()
                .inset(insetAll())
        ) {
            VideoPlayer(
                localUri = uiState.uri,
                shouldPlay = true,
                isScrolling = false,
                progressPollFrequencyInMillis = 50L,
                onProgressChanged = onProgressChanged,
                isRepeat = false, // OutOfMemory on repeat
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start,
            ) {
                Progress(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    progress = uiState.playbackProgress,
                    isDashed = false,
                    backColor = AppTheme.specificColorScheme.white_40,
                    fillColor = AppTheme.specificColorScheme.white,
                    height = 6.dp,
                )
                Spacer(modifier = Modifier.height(16.dp))
                IconButton(onClick = onBackClicked) {
                    Icon(
                        painter = AppTheme.specificIcons.back.get(),
                        contentDescription = null,
                        tint = AppTheme.specificColorScheme.white,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                SimpleButtonInlineL(
                    onClick = onProceedClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    SimpleButtonContent(
                        text = StringKey.PreviewVideoActionProceed.textValue(),
                        textColor = AppTheme.specificColorScheme.white,
                    )
                }
            }
        }
    }
}