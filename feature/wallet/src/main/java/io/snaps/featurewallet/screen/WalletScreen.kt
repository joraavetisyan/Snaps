package io.snaps.featurewallet.screen

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.ui.MainHeader
import io.snaps.baseprofile.ui.MainHeaderState
import io.snaps.corecommon.container.IconValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.MoneyDto
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.doOnClick
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAll
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonGreyM
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.status.SimpleBottomDialogUI
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featurewallet.ScreenNavigator
import io.snaps.featurewallet.viewmodel.WalletViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WalletScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<WalletViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    val headerState by viewModel.headerUiState.collectAsState()

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

    viewModel.headerCommand.collectAsCommand {
        when (it) {
            MainHeaderHandler.Command.OpenProfileScreen -> router.toProfileScreen()
            MainHeaderHandler.Command.OpenWalletScreen -> { /*todo*/ }
        }
    }

    BackHandler(enabled = sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            when (val dialog = uiState.bottomDialogType) {
                is WalletViewModel.BottomDialogType.SelectCurrency -> SelectCurrencyDialog(
                    currencies = uiState.currencies,
                )
                is WalletViewModel.BottomDialogType.TopUp -> TopUpDialog(
                    title = "Top up BNB (BEP-20)",
                    address = uiState.address,
                    qr = dialog.qr,
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
                    text = StringKey.WalletTitle.textValue().get(),
                    style = AppTheme.specificTypography.titleMedium,
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
                    token = uiState.address,
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
                    style = AppTheme.specificTypography.titleMedium,
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
                                .background(
                                    color = AppTheme.specificColorScheme.white,
                                    shape = AppTheme.shapes.medium,
                                )
                                .border(
                                    width = 1.dp,
                                    color = AppTheme.specificColorScheme.grey.copy(alpha = 0.5f),
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
        border = BorderStroke(
            width = 1.dp,
            color = AppTheme.specificColorScheme.darkGrey.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
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
                    .padding(),
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
        border = BorderStroke(
            width = 1.dp,
            color = AppTheme.specificColorScheme.darkGrey.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .weight(1f)
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
                style = AppTheme.specificTypography.titleSmall,
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
                    .background(
                        color = AppTheme.specificColorScheme.white,
                        shape = AppTheme.shapes.medium,
                    )
                    .border(
                        width = 1.dp,
                        color = AppTheme.specificColorScheme.grey.copy(alpha = 0.5f),
                        shape = AppTheme.shapes.medium,
                    )
                    .padding(horizontal = 12.dp),
            )
        }
    }
}

@Composable
private fun TopUpDialog(
    qr: Bitmap?,
    title: String,
    address: String,
    onTokenCopyClicked: () -> Unit,
) {
    SimpleBottomDialogUI(title.textValue()) {
        item {
            Box(
                modifier = Modifier
                    .size(164.dp)
                    .padding(16.dp)
                    .background(
                        color = AppTheme.specificColorScheme.white,
                        shape = AppTheme.shapes.extraLarge,
                    ),
            ) {
                qr?.let {
                    Image(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxSize(),
                        bitmap = it.asImageBitmap(),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = null,
                    )
                }
            }
            SimpleButtonGreyM(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                onClick = onTokenCopyClicked,
            ) {
                SimpleButtonContent(
                    text = address.textValue(),
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