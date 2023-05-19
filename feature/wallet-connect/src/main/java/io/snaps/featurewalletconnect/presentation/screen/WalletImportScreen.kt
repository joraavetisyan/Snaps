package io.snaps.featurewalletconnect.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.corecommon.container.IconValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.dialog.SimpleAlertDialogUi
import io.snaps.coreuicompose.uikit.duplicate.OnBackIconClick
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuicompose.uikit.input.SimpleTextField
import io.snaps.coreuicompose.uikit.status.FullScreenLoaderUi
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featurewalletconnect.ScreenNavigator
import io.snaps.featurewalletconnect.presentation.viewmodel.WalletImportViewModel

@Composable
fun WalletImportScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<WalletImportViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    viewModel.command.collectAsCommand {}

    WalletImportScreen(
        uiState = uiState,
        onContinueButtonClicked = viewModel::onContinueButtonClicked,
        onPhraseValueChanged = viewModel::onPhraseValueChanged,
        onDialogDismissRequested = viewModel::onDialogDismissRequested,
        navigationIcon = if (navHostController.previousBackStackEntry?.id != null) {
            AppTheme.specificIcons.back to router::back
        } else null,
    )
    FullScreenLoaderUi(isLoading = uiState.isLoading)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WalletImportScreen(
    uiState: WalletImportViewModel.UiState,
    onContinueButtonClicked: () -> Unit,
    onPhraseValueChanged: (String) -> Unit,
    onDialogDismissRequested: () -> Unit,
    navigationIcon: Pair<IconValue, OnBackIconClick>?,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SimpleTopAppBar(
                title = StringKey.WalletImportTitle.textValue(),
                titleTextStyle = AppTheme.specificTypography.titleMedium,
                scrollBehavior = scrollBehavior,
                navigationIcon = navigationIcon,
            )
        },
        floatingActionButton = {
            SimpleButtonActionM(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                onClick = onContinueButtonClicked,
                enabled = uiState.isContinueEnabled,
            ) {
                SimpleButtonContent(text = StringKey.ActionContinue.textValue())
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 12.dp),
        ) {
            Text(
                text = StringKey.WalletImportMessageEnterPhrase.textValue().get(),
                style = AppTheme.specificTypography.titleSmall,
                color = AppTheme.specificColorScheme.textSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                textAlign = TextAlign.Center,
            )
            SimpleTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                onValueChange = onPhraseValueChanged,
                value = uiState.words,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                placeholder = {
                    Text(
                        text = StringKey.WalletImportHint.textValue().get(),
                        style = AppTheme.specificTypography.titleSmall,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                    )
                },
                minLines = 5,
                shape = AppTheme.shapes.medium,
            )
            Text(
                text = StringKey.WalletImportMessagePhraseExplanation.textValue().get(),
                style = AppTheme.specificTypography.bodySmall,
                color = AppTheme.specificColorScheme.textSecondary,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
    when (uiState.dialog) {
        WalletImportViewModel.Dialog.DeviceNotSecured -> SimpleAlertDialogUi(
            text = StringKey.DeviceNotSecuredDialogMessage.textValue(),
            title = StringKey.DeviceNotSecuredDialogTitle.textValue(),
            buttonText = StringKey.ActionClose.textValue(),
            onClickRequest = onDialogDismissRequested,
        )
        null -> Unit
    }
}