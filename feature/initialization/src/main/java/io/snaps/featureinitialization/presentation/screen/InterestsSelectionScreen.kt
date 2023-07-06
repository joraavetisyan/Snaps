package io.snaps.featureinitialization.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAll
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonGreyM
import io.snaps.coreuicompose.uikit.status.FullScreenLoaderUi
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featureinitialization.ScreenNavigator
import io.snaps.featureinitialization.presentation.viewmodel.InterestsSelectionViewModel

@Composable
fun InterestSelectionScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<InterestsSelectionViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    viewModel.command.collectAsCommand {
        when (it) {
            InterestsSelectionViewModel.Command.CloseScreen -> router.back()
        }
    }

    InterestSelectionScreen(
        uiState = uiState,
        onSkipClicked = viewModel::onSkipButtonClicked,
        onSelectClicked = viewModel::onSelectButtonClicked,
    )

    FullScreenLoaderUi(isLoading = uiState.isLoading)
}

@Composable
private fun InterestSelectionScreen(
    uiState: InterestsSelectionViewModel.UiState,
    onSkipClicked: () -> Unit,
    onSelectClicked: () -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButtons(
                isSelectEnabled = uiState.isSelectButtonEnabled,
                onSelectClicked = onSelectClicked,
                onSkipClicked = onSkipClicked,
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .inset(insetAll()),
        ) {
            Text(
                text = StringKey.InterestsSelectionTitle.textValue().get(),
                style = AppTheme.specificTypography.titleMedium,
                color = AppTheme.specificColorScheme.textPrimary,
            )
            Text(
                text = StringKey.InterestsSelectionMessage.textValue().get(),
                style = AppTheme.specificTypography.titleSmall,
                color = AppTheme.specificColorScheme.textSecondary,
            )
            Spacer(modifier = Modifier.height(16.dp))
            uiState.interests.Content(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun FloatingActionButtons(
    isSelectEnabled: Boolean,
    onSkipClicked: () -> Unit,
    onSelectClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SimpleButtonGreyM(
            modifier = Modifier.weight(1f),
            onClick = onSkipClicked,
        ) {
            SimpleButtonContent(StringKey.InterestsSelectionActionSkip.textValue())
        }
        SimpleButtonActionM(
            modifier = Modifier.weight(1f),
            onClick = onSelectClicked,
            enabled = isSelectEnabled,
        ) {
            SimpleButtonContent(StringKey.InterestsSelectionActionSelect.textValue())
        }
    }
}