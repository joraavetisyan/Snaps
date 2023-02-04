package io.snaps.featurewallet.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.baseprofile.ui.MainHeader
import io.snaps.baseprofile.ui.MainHeaderState
import io.snaps.corecommon.container.IconValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.MoneyDto
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonGreyM
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featurewallet.ScreenNavigator
import io.snaps.featurewallet.viewmodel.WalletViewModel
import androidx.compose.runtime.*
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.*
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.status.SimpleBottomDialogUI
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WalletScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<WalletViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    val headerState by viewModel.headerState.collectAsState()

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    val coroutineScope = rememberCoroutineScope()

    viewModel.command.collectAsCommand {
        when (it) {
            WalletViewModel.Command.ShowBottomDialog -> coroutineScope.launch { sheetState.show() }
            WalletViewModel.Command.HideBottomDialog -> coroutineScope.launch { sheetState.hide() }
            WalletViewModel.Command.OpenWithdrawScreen -> router.toWithdrawScreen()
        }
    }

    BackHandler(enabled = sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            when (uiState.bottomDialogType) {
                WalletViewModel.BottomDialogType.SelectCurrency -> SelectCurrencyDialog(
                    currencies = uiState.currencies,
                )
                WalletViewModel.BottomDialogType.TopUp -> TopUpDialog(
                    title = "Top up BNB (BEP-20)",
                    token = uiState.token,
                    onTokenCopyClicked = {},
                )
            }
        },
    ) {
        WalletScreen(
            uiState = uiState,
            headerState = headerState.value,
            onBackClicked = router::back,
            onTokenCopyClicked = {},
            onTopUpClicked = viewModel::onTopUpClicked,
            onWithdrawClicked = viewModel::onWithdrawClicked,
            onExchangeClicked = viewModel::onExchangeClicked,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WalletScreen(
    uiState: WalletViewModel.UiState,
    headerState: MainHeaderState,
    onBackClicked: () -> Boolean,
    onTokenCopyClicked: () -> Unit,
    onTopUpClicked: () -> Unit,
    onWithdrawClicked: () -> Unit,
    onExchangeClicked: () -> Unit,
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
            MainHeader(uiState = headerState)
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
                    text = StringKey.WalletTitle.textValue().get(),
                    style = AppTheme.specificTypography.titleLarge,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
            }
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp)
            ) {
                Balance(
                    amount = uiState.selectedCurrency,
                    token = uiState.token,
                    onTokenCopyClicked = onTokenCopyClicked,
                )
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(top = 12.dp, bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OperationType(
                        title = StringKey.WalletTitleTopUp.textValue().get().text,
                        image = AppTheme.specificIcons.topUp,
                        onClick = onTopUpClicked,
                    )
                    OperationType(
                        title = StringKey.WalletTitleWithdraw.textValue().get().text,
                        image = AppTheme.specificIcons.withdraw,
                        onClick = onWithdrawClicked,
                    )
                    OperationType(
                        title = StringKey.WalletTitleExchange.textValue().get().text,
                        image = AppTheme.specificIcons.exchange,
                        onClick = onExchangeClicked,
                    )
                }
                Text(
                    text = StringKey.WalletTitleBalance.textValue().get().text,
                    style = AppTheme.specificTypography.headlineSmall,
                    color = AppTheme.specificColorScheme.textPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)
                ) {
                    uiState.currencies.forEach { item ->
                        item.Content(
                            modifier = Modifier
                                .shadow(elevation = 8.dp, shape = AppTheme.shapes.medium)
                                .background(
                                    color = AppTheme.specificColorScheme.white,
                                    shape = AppTheme.shapes.medium,
                                )
                                .border(
                                    width = 1.dp,
                                    color = AppTheme.specificColorScheme.grey,
                                    shape = AppTheme.shapes.medium,
                                )
                                .padding(horizontal = 12.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Balance(
    amount: MoneyDto,
    token: String,
    onTokenCopyClicked: () -> Unit,
) {
    Card(
        shape = AppTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = AppTheme.specificColorScheme.white),
        border = BorderStroke(width = 1.dp, color = AppTheme.specificColorScheme.darkGrey),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = AppTheme.shapes.medium)
            .padding(horizontal = 12.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = StringKey.WalletFieldTotal.textValue().get().text,
                style = AppTheme.specificTypography.bodySmall,
                color = AppTheme.specificColorScheme.textSecondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            Text(
                text = amount.getFormattedMoneyWithCurrency(),
                style = AppTheme.specificTypography.titleLarge,
                color = AppTheme.specificColorScheme.textPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                textAlign = TextAlign.Center,
            )
            Text(
                text = "≈ ${amount.getFormattedMoneyWithCurrency()}",
                style = AppTheme.specificTypography.bodySmall,
                color = AppTheme.specificColorScheme.textSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                textAlign = TextAlign.Center,
            )
            SimpleButtonGreyM(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding()
                    .shadow(elevation = 16.dp, shape = CircleShape),
                onClick = onTokenCopyClicked,
            ) {
                SimpleButtonContent(
                    text = token.textValue(),
                    iconRight = AppTheme.specificIcons.copy,
                )
            }
        }
    }
}

@Composable
private fun RowScope.OperationType(
    title: String,
    image: IconValue,
    onClick: () -> Unit,
) {
    Card(
        shape = AppTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = AppTheme.specificColorScheme.white),
        border = BorderStroke(width = 1.dp, color = AppTheme.specificColorScheme.darkGrey),
        modifier = Modifier
            .weight(1f)
            .shadow(elevation = 8.dp, shape = AppTheme.shapes.medium)
            .doOnClick(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = image.get(),
                tint = AppTheme.specificColorScheme.white,
                contentDescription = null,
                modifier = Modifier
                    .background(color = AppTheme.specificColorScheme.uiAccent, shape = CircleShape)
                    .padding(12.dp),
            )
            Text(
                text = title,
                style = AppTheme.specificTypography.titleMedium,
                color = AppTheme.specificColorScheme.textPrimary,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun SelectCurrencyDialog(
    currencies: List<CellTileState>,
) {
    SimpleBottomDialogUI(StringKey.WalletTitleSelectCurrency.textValue()) {
        item { 
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(currencies) {
            it.Content(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .shadow(elevation = 8.dp, shape = AppTheme.shapes.medium)
                    .background(
                        color = AppTheme.specificColorScheme.white,
                        shape = AppTheme.shapes.medium,
                    )
                    .border(
                        width = 1.dp,
                        color = AppTheme.specificColorScheme.grey,
                        shape = AppTheme.shapes.medium,
                    )
                    .padding(horizontal = 12.dp),
            )
        }
    }
}

@Composable
private fun TopUpDialog(
    title: String,
    token: String,
    onTokenCopyClicked: () -> Unit,
) {
    SimpleBottomDialogUI(title.textValue()) {
        item {
            Box(
                modifier = Modifier
                    .size(164.dp)
                    .padding(16.dp)
                    .background(
                        color = AppTheme.specificColorScheme.darkGrey,
                        shape = AppTheme.shapes.extraLarge,
                    ),
            ) {}
            SimpleButtonGreyM(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .shadow(elevation = 16.dp, shape = CircleShape),
                onClick = onTokenCopyClicked,
            ) {
                SimpleButtonContent(
                    text = token.textValue(),
                    iconRight = AppTheme.specificIcons.copy,
                )
            }
            Text(
                text = StringKey.WalletMessageTopUp.textValue().get().text,
                style = AppTheme.specificTypography.bodySmall,
                color = AppTheme.specificColorScheme.textSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 24.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}