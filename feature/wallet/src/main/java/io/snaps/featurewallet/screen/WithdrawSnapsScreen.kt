package io.snaps.featurewallet.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.basewallet.ui.LimitedGasDialog
import io.snaps.basewallet.ui.LimitedGasDialogHandler
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.CoinType
import io.snaps.corecommon.strings.StringKey
import io.snaps.corenavigation.base.popBackStackWithResult
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionS
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuicompose.uikit.input.SimpleTextField
import io.snaps.coreuicompose.uikit.status.FullScreenLoaderUi
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featurewallet.ScreenNavigator
import io.snaps.featurewallet.viewmodel.WithdrawSnapsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun WithdrawSnapsScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<WithdrawSnapsViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    val limitedGasState by viewModel.limitedGasState.collectAsState()

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    val coroutineScope = rememberCoroutineScope()

    val keyboardController = LocalSoftwareKeyboardController.current

    viewModel.command.collectAsCommand {
        when (it) {
            is WithdrawSnapsViewModel.Command.CloseScreenOnSuccess -> navHostController.popBackStackWithResult(it.data)
        }
    }
    viewModel.limitedGasCommand.collectAsCommand {
        when (it) {
            LimitedGasDialogHandler.Command.ShowBottomDialog -> coroutineScope.launch {
                keyboardController?.hide()
                sheetState.show()
            }
            LimitedGasDialogHandler.Command.HideBottomDialog -> coroutineScope.launch { sheetState.hide() }
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            when (val dialog = limitedGasState.bottomDialog) {
                is LimitedGasDialogHandler.BottomDialog.Refill -> LimitedGasDialog(
                    onRefillClick = dialog.onRefillClicked,
                )
                null -> Unit
            }
        },
    ) {
        WithdrawSnapsScreen(
            uiState = uiState,
            onBackClicked = router::back,
            onAmountValueChanged = viewModel::onAmountValueChanged,
            onMaxButtonClicked = viewModel::onMaxButtonClicked,
            onSendClicked = viewModel::onSendClicked,
            onCardNumberValueChanged = viewModel::onCardNumberValueChanged,
            onRepeatCardNumberValueChanged = viewModel::onRepeatCardNumberValueChanged,
        )
    }

    FullScreenLoaderUi(isLoading = uiState.isLoading)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WithdrawSnapsScreen(
    uiState: WithdrawSnapsViewModel.UiState,
    onAmountValueChanged: (String) -> Unit,
    onSendClicked: () -> Unit,
    onMaxButtonClicked: () -> Unit,
    onBackClicked: () -> Boolean,
    onCardNumberValueChanged: (String) -> Unit,
    onRepeatCardNumberValueChanged: (String) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            // todo localize
            SimpleTopAppBar(
                title = "Sell ${CoinType.SNPS.symbol}".textValue(),
                navigationIcon = AppTheme.specificIcons.back to onBackClicked,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .inset(insetAllExcludeTop()),
        ) {
            Text(
                // todo localize
                text = "All data is encrypted and never shared with third parties",
                style = AppTheme.specificTypography.bodySmall,
                color = AppTheme.specificColorScheme.textSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
            )
            SimpleTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                onValueChange = uiState.amountFormatter.onValueChanged(onAmountValueChanged),
                value = uiState.amountValue,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next,
                ),
                placeholder = {
                    Text(
                        // todo localize
                        text = "Transfer amount",
                        style = AppTheme.specificTypography.titleSmall,
                    )
                },
                trailingIcon = {
                    SimpleButtonActionS(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        onClick = onMaxButtonClicked,
                    ) {
                        // todo localize
                        SimpleButtonContent(text = "Max".textValue())
                    }
                },
            )
            SimpleTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                onValueChange = uiState.cardNumberFormatter.onValueChanged(onCardNumberValueChanged),
                value = uiState.cardNumberValue,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                placeholder = {
                    Text(
                        // todo localize
                        text = "Enter card number",
                        style = AppTheme.specificTypography.titleSmall,
                    )
                },
                /*visualTransformation = uiState.cardNumberFormatter.visualTransformation(
                        inputtedPartColor = AppTheme.specificColorScheme.textPrimary },
                        otherPartColor = Color.Transparent,
                    ),*/
            )
            SimpleTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                onValueChange = uiState.cardNumberFormatter.onValueChanged(onRepeatCardNumberValueChanged),
                value = uiState.repeatCardNumberValue,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                placeholder = {
                    Text(
                        // todo localize
                        text = "Repeat card number",
                        style = AppTheme.specificTypography.titleSmall,
                    )
                },
                /*visualTransformation = uiState.cardNumberFormatter.visualTransformation(
                        inputtedPartColor = AppTheme.specificColorScheme.textPrimary },
                        otherPartColor = Color.Transparent,
                    ),*/
            )
            Text(
                // todo localize
                text = "Текущий баланс: ${uiState.snpWalletModel?.coinValue?.getFormatted().orEmpty()}".textValue().get(),
                style = AppTheme.specificTypography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                textAlign = TextAlign.End,
            )
            Spacer(modifier = Modifier.weight(1f))
            Divider(
                color = AppTheme.specificColorScheme.darkGrey.copy(alpha = 0.5f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 20.dp)
            )
            @Composable
            fun Line(title: TextValue, value: TextValue) = Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = title.get(),
                    style = AppTheme.specificTypography.bodySmall,
                    color = AppTheme.specificColorScheme.textSecondary,
                    modifier = Modifier.padding(12.dp),
                )
                Text(
                    text = value.get(),
                    style = AppTheme.specificTypography.bodySmall,
                    color = AppTheme.specificColorScheme.textPrimary,
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
            }
            // todo localize
            Line("Commission".textValue(), "${uiState.commission}%".textValue())
            // todo localize
            Line("Total".textValue(), uiState.total.getFormatted().textValue())
            SimpleButtonActionM(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 20.dp),
                onClick = onSendClicked,
                enabled = uiState.isSendEnabled,
            ) {
                SimpleButtonContent(text = StringKey.ActionSend.textValue())
            }
        }
    }
}