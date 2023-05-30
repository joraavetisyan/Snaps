package io.snaps.featurewalletconnect.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuicompose.uikit.dialog.SimpleAlertDialogUi
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
import io.snaps.featurewalletconnect.ScreenNavigator
import io.snaps.featurewalletconnect.presentation.viewmodel.MnemonicsVerificationViewModel
import io.snaps.featurewalletconnect.presentation.viewmodel.SelectionUiModel
import io.snaps.featurewalletconnect.presentation.viewmodel.WordUiModel

@Composable
fun MnemonicListVerificationScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<MnemonicsVerificationViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    viewModel.command.collectAsCommand {
        when (it) {
            MnemonicsVerificationViewModel.Command.OpenCreatedWalletScreen -> router.toWalletConnectedScreen()
        }
    }

    MnemonicListVerificationScreen(
        uiState = uiState,
        onContinueButtonClicked = viewModel::onContinueButtonClicked,
        onBackClicked = router::back,
        onWordItemClicked = viewModel::onWordItemClicked,
        onAnimationFinished = viewModel::onAnimationFinished,
        onDialogDismissRequested = viewModel::onDialogDismissRequested,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MnemonicListVerificationScreen(
    uiState: MnemonicsVerificationViewModel.UiState,
    onContinueButtonClicked: () -> Unit,
    onBackClicked: () -> Boolean,
    onDialogDismissRequested: () -> Unit,
    onWordItemClicked: (SelectionUiModel, WordUiModel) -> Unit,
    onAnimationFinished: (SelectionUiModel, WordUiModel) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val topBarTitle = LocalStringHolder.current(StringKey.VerificationTitle)
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SimpleTopAppBar(
                title = {
                    Text(topBarTitle)
                },
                titleTextStyle = AppTheme.specificTypography.titleMedium,
                scrollBehavior = scrollBehavior,
                navigationIcon = AppTheme.specificIcons.back to onBackClicked,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = LocalStringHolder.current(StringKey.VerificationMessage),
                style = AppTheme.specificTypography.titleSmall,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp, bottom = 24.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = AppTheme.specificColorScheme.textSecondary,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                uiState.selections.forEach {
                    SelectionBlock(
                        selection = it,
                        onWordItemClicked = onWordItemClicked,
                        onAnimationFinished = onAnimationFinished,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            SimpleButtonActionM(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                onClick = onContinueButtonClicked,
                enabled = uiState.isContinueButtonEnabled,
            ) {
                SimpleButtonContent(text = StringKey.ActionContinue.textValue())
            }
        }
    }
    when (uiState.dialog) {
        MnemonicsVerificationViewModel.Dialog.DeviceNotSecured -> SimpleAlertDialogUi(
            text = StringKey.DeviceNotSecuredDialogMessage.textValue(),
            title = StringKey.DeviceNotSecuredDialogTitle.textValue(),
            buttonText = StringKey.ActionClose.textValue(),
            onClickRequest = onDialogDismissRequested,
        )
        null -> Unit
    }
}

@Composable
private fun SelectionBlock(
    selection: SelectionUiModel,
    onWordItemClicked: (SelectionUiModel, WordUiModel) -> Unit,
    onAnimationFinished: (SelectionUiModel, WordUiModel) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AppTheme.specificColorScheme.white, shape = AppTheme.shapes.medium)
            .border(
                width = 1.dp,
                color = AppTheme.specificColorScheme.darkGrey.copy(alpha = 0.5f),
                shape = AppTheme.shapes.medium,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${selection.ordinal}.",
            color = AppTheme.specificColorScheme.textSecondary,
            style = AppTheme.specificTypography.titleSmall,
            modifier = Modifier.padding(start = 24.dp),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(24.dp),
        ) {
            selection.words.forEach { item ->
                SelectorTile(
                    data = SelectorTileData(
                        text = item.text.textValue(),
                        status = item.status,
                        clickListener = { onWordItemClicked(selection, item) },
                        onAnimationFinished = { onAnimationFinished(selection, item) },
                    ),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}