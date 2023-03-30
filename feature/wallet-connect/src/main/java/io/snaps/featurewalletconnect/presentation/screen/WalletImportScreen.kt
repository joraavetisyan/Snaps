package io.snaps.featurewalletconnect.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuicompose.uikit.input.SimpleTextField
import io.snaps.coreuicompose.uikit.status.FullScreenLoaderUi
import io.snaps.coreuicompose.uikit.status.SimpleAlertDialogUi
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
        onBackClicked = router::back,
        onDialogDismissRequested = viewModel::onDialogDismissRequested,
    )
    FullScreenLoaderUi(isLoading = uiState.isLoading)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WalletImportScreen(
    uiState: WalletImportViewModel.UiState,
    onContinueButtonClicked: () -> Unit,
    onPhraseValueChanged: (String, Int) -> Unit,
    onBackClicked: () -> Boolean,
    onDialogDismissRequested: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SimpleTopAppBar(
                title = {
                    Text(StringKey.WalletImportTitle.textValue().get())
                },
                titleTextStyle = AppTheme.specificTypography.titleMedium,
                scrollBehavior = scrollBehavior,
                navigationIcon = AppTheme.specificIcons.back to onBackClicked,
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 12.dp)
                .wrapContentHeight()
                .imePadding()
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = StringKey.WalletImportMessageEnterPhrases.textValue().get(),
                style = AppTheme.specificTypography.titleSmall,
                color = AppTheme.specificColorScheme.textSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
                textAlign = TextAlign.Center,
            )
            uiState.words.forEachIndexed { index, phrase ->
                SimpleTextField(
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { value ->
                        onPhraseValueChanged(value, index)
                    },
                    value = phrase,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = if (index != uiState.words.lastIndex) {
                            ImeAction.Next
                        } else ImeAction.Done,
                    ),
                    placeholder = {
                        Text(
                            text = StringKey.WalletImportHint
                                .textValue((index + 1).toString())
                                .get(),
                            style = AppTheme.specificTypography.titleSmall,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                    },
//                    visualTransformation = OrdinalNumberFormatter(index + 1),
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                )
                if (index != uiState.words.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            SimpleButtonActionM(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                onClick = onContinueButtonClicked,
                enabled = uiState.isContinueButtonEnabled,
            ) {
                SimpleButtonContent(text = StringKey.ActionContinue.textValue())
            }
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