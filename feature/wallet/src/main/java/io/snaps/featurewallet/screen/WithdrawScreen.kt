package io.snaps.featurewallet.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.ui.MainHeader
import io.snaps.baseprofile.ui.MainHeaderState
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAll
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionS
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.input.SimpleTextField
import io.snaps.coreuicompose.uikit.status.FullScreenLoaderUi
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
import io.snaps.featurewallet.ScreenNavigator
import io.snaps.featurewallet.viewmodel.CryptoSendHandler
import io.snaps.featurewallet.viewmodel.WithdrawViewModel

@Composable
fun WithdrawScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<WithdrawViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    val headerState by viewModel.headerUiState.collectAsState()
    val cryptoSendState by viewModel.cryptoSendState.collectAsState()

    viewModel.command.collectAsCommand {}

    viewModel.headerCommand.collectAsCommand {
        when (it) {
            MainHeaderHandler.Command.OpenProfileScreen -> router.toProfileScreen()
            MainHeaderHandler.Command.OpenWalletScreen -> router.back()
        }
    }

    viewModel.cryptoSendCommand.collectAsCommand {
        when (it) {
            CryptoSendHandler.Command.CloseScreen -> router.back()
        }
    }

    WithdrawScreen(
        uiState = uiState,
        cryptoSendState = cryptoSendState,
        headerState = headerState.value,
        onBackClicked = router::back,
        onAddressValueChanged = viewModel::onAddressValueChanged,
        onAmountValueChanged = viewModel::onAmountValueChanged,
        onConfirmTransactionClicked = viewModel::onConfirmTransactionClicked,
        onMaxButtonClicked = viewModel::onMaxButtonClicked,
    )

    FullScreenLoaderUi(isLoading = cryptoSendState.isLoading)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WithdrawScreen(
    uiState: WithdrawViewModel.UiState,
    cryptoSendState: CryptoSendHandler.UiState,
    headerState: MainHeaderState,
    onAddressValueChanged: (String) -> Unit,
    onAmountValueChanged: (String) -> Unit,
    onConfirmTransactionClicked: () -> Unit,
    onMaxButtonClicked: () -> Unit,
    onBackClicked: () -> Boolean,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = AppTheme.specificColorScheme.uiContentBg,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .inset(insetAll()),
        ) {
            MainHeader(state = headerState)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = AppTheme.specificIcons.back.get(),
                    tint = AppTheme.specificColorScheme.darkGrey,
                    contentDescription = null,
                    modifier = Modifier.clickable { onBackClicked() }
                )
                Text(
                    text = StringKey.WithdrawTitle.textValue(uiState.walletModel.symbol).get(),
                    style = AppTheme.specificTypography.titleMedium,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            SimpleTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                onValueChange = onAddressValueChanged,
                value = uiState.addressValue,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                placeholder = {
                    Text(
                        text = LocalStringHolder.current(StringKey.WithdrawHintAddress),
                        style = AppTheme.specificTypography.titleSmall,
                    )
                },
                maxLines = 1,
            )
            SimpleTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                onValueChange = onAmountValueChanged,
                value = uiState.amountValue,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done,
                ),
                placeholder = {
                    Text(
                        text = LocalStringHolder.current(StringKey.WithdrawHintAmount),
                        style = AppTheme.specificTypography.titleSmall,
                    )
                },
                trailingIcon = {
                    SimpleButtonActionS(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        onClick = onMaxButtonClicked,
                    ) {
                        SimpleButtonContent(
                            text = StringKey.WithdrawActionMax.textValue()
                        )
                    }
                },
                maxLines = 1,
            )
            Text(
                text = StringKey.WithdrawFieldAvailable.textValue(uiState.availableAmount)
                    .get(),
                style = AppTheme.specificTypography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                textAlign = TextAlign.End,
            )
            Spacer(modifier = Modifier.weight(1f))
            Divider(
                color = AppTheme.specificColorScheme.darkGrey.copy(alpha = 0.5f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 20.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = StringKey.WithdrawFieldTransactionFee.textValue().get(),
                    style = AppTheme.specificTypography.bodySmall,
                    color = AppTheme.specificColorScheme.textSecondary,
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
                Text(
                    text = cryptoSendState.transactionFee,
                    style = AppTheme.specificTypography.bodySmall,
                    color = AppTheme.specificColorScheme.textPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    textAlign = TextAlign.End,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = StringKey.WithdrawFieldTotal.textValue().get(),
                    style = AppTheme.specificTypography.bodySmall,
                    color = AppTheme.specificColorScheme.textSecondary,
                    modifier = Modifier.padding(12.dp),
                )
                Text(
                    text = cryptoSendState.totalAmount,
                    style = AppTheme.specificTypography.bodySmall,
                    color = AppTheme.specificColorScheme.textPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    textAlign = TextAlign.End,
                )
            }
            SimpleButtonActionM(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 20.dp),
                onClick = if (cryptoSendState.isSendEnabled) {
                    cryptoSendState.onSendClicked
                } else {
                    onConfirmTransactionClicked
                },
                enabled = uiState.isConfirmEnabled || cryptoSendState.isSendEnabled,
            ) {
                SimpleButtonContent(
                    text = if (cryptoSendState.isSendEnabled) {
                        StringKey.WithdrawActionSendTransaction
                    } else {
                        StringKey.WithdrawActionConfirmTransaction
                    }.textValue(),
                )
            }
        }
    }
}