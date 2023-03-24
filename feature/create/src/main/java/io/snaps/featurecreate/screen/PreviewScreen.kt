package io.snaps.featurecreate.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.baseplayer.ui.VideoPlayer
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAll
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.input.SimpleTextField
import io.snaps.coreuicompose.uikit.status.FullScreenLoaderUi
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
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
        onTitleChanged = viewModel::onTitleChanged,
        onDescriptionChanged = viewModel::onDescriptionChanged,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun PreviewScreen(
    uiState: PreviewViewModel.UiState,
    onBackClicked: () -> Boolean,
    onProceedClicked: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .inset(insetAll())
    ) {
        VideoPlayer(
            localUri = uiState.uri,
            shouldPlay = true,
            isMuted = false,
            isScrolling = false,
            onMuted = {},
            isRepeat = false,
        )
        Column(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SimpleTextField(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = onTitleChanged,
                value = uiState.titleValue,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                placeholder = {
                    Text(
                        text = LocalStringHolder.current(StringKey.PreviewVideoHintTitle),
                        style = AppTheme.specificTypography.titleSmall,
                    )
                },
                maxLines = 1,
            )
            SimpleTextField(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = onDescriptionChanged,
                value = uiState.descriptionValue,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                placeholder = {
                    Text(
                        text = LocalStringHolder.current(StringKey.PreviewVideoHintDescription),
                        style = AppTheme.specificTypography.titleSmall,
                    )
                },
                maxLines = 1,
            )
            Row(
                Modifier
                    .fillMaxWidth()
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
    }
    FullScreenLoaderUi(isLoading = uiState.isLoading)
}