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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.ui.MainHeader
import io.snaps.baseprofile.ui.MainHeaderState
import io.snaps.basewallet.domain.TotalBalanceModel
import io.snaps.corecommon.container.IconValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.WalletAddress
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.doOnClick
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAll
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonGreyM
import io.snaps.coreuicompose.uikit.button.SimpleButtonGreyS
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.other.TitleSlider
import io.snaps.coreuicompose.uikit.scroll.ScrollEndDetectLazyColumn
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

    val clipboardManager = LocalClipboardManager.current

    viewModel.command.collectAsCommand {
        when (it) {
            WalletViewModel.Command.ShowBottomDialog -> coroutineScope.launch { sheetState.show() }
            WalletViewModel.Command.HideBottomDialog -> coroutineScope.launch { sheetState.hide() }
            is WalletViewModel.Command.OpenWithdrawScreen -> router.toWithdrawScreen(it.wallet)
            is WalletViewModel.Command.OpenExchangeScreen -> router.toExchangeScreen(it.wallet)
        }
    }

    viewModel.headerCommand.collectAsCommand {
        when (it) {
            MainHeaderHandler.Command.OpenProfileScreen -> router.toProfileScreen()
            MainHeaderHandler.Command.OpenWalletScreen -> Unit
        }
    }

    BackHandler(enabled = sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }

    fun onAddressCopyClicked(address: WalletAddress) {
        clipboardManager.setText(AnnotatedString(address))
        viewModel.onAddressCopied()
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            when (val dialog = uiState.bottomDialogType) {
                is WalletViewModel.BottomDialogType.SelectWallet -> SelectWalletDialog(
                    wallets = dialog.wallets,
                )
                is WalletViewModel.BottomDialogType.TopUp -> TopUpDialog(
                    title = StringKey.WalletDialogTitleTopUp.textValue(dialog.title),
                    address = dialog.address,
                    qr = dialog.qr,
                    onAddressCopyClicked = { onAddressCopyClicked(dialog.address) },
                )
            }
        },
    ) {
        WalletScreen(
            uiState = uiState,
            headerState = headerState.value,
            onBackClicked = router::back,
            onAddressCopyClicked = ::onAddressCopyClicked,
            onTopUpClicked = viewModel::onTopUpClicked,
            onWithdrawClicked = viewModel::onWithdrawClicked,
            onRewardsWithdrawClicked = viewModel::onRewardsWithdrawClicked,
            onExchangeClicked = viewModel::onExchangeClicked,
            onRewardsOpened = viewModel::onRewardsOpened,
            onDropdownMenuItemClicked = viewModel::onDropdownMenuItemClicked,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
private fun WalletScreen(
    uiState: WalletViewModel.UiState,
    headerState: MainHeaderState,
    onBackClicked: () -> Boolean,
    onAddressCopyClicked: (WalletAddress) -> Unit,
    onTopUpClicked: () -> Unit,
    onWithdrawClicked: () -> Unit,
    onRewardsWithdrawClicked: () -> Unit,
    onExchangeClicked: () -> Unit,
    onRewardsOpened: () -> Unit,
    onDropdownMenuItemClicked: (WalletViewModel.FilterOptions) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val wallet = StringKey.WalletTitle.textValue()
    val awards = StringKey.WalletTitleRewards.textValue()

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

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
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = AppTheme.specificIcons.back.get(),
                    tint = AppTheme.specificColorScheme.darkGrey,
                    contentDescription = null,
                    modifier = Modifier.clickable { onBackClicked() }
                )
                TitleSlider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    items = listOf(wallet, awards),
                    selectedItemIndex = pagerState.currentPage,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(it)
                        }
                    },
                )
            }
            HorizontalPager(
                count = 2,
                state = pagerState,
            ) {
                when (currentPage) {
                    0 -> Wallet(
                        uiState = uiState,
                        onAddressCopyClicked = onAddressCopyClicked,
                        onTopUpClicked = onTopUpClicked,
                        onWithdrawClicked = onWithdrawClicked,
                        onExchangeClicked = onExchangeClicked,
                    )
                    1 -> Rewards(
                        transactions = when (uiState.filterOptions) {
                            WalletViewModel.FilterOptions.Unlocked -> uiState.unlockedTransactions
                            WalletViewModel.FilterOptions.Locked -> uiState.lockedTransactions
                        },
                        rewards = uiState.rewards,
                        onOpened = onRewardsOpened,
                        filterOptions = uiState.filterOptions,
                        onWithdrawClicked = onRewardsWithdrawClicked,
                        onDropdownMenuItemClicked = onDropdownMenuItemClicked,
                    )
                }
            }
        }
    }
}

@Composable
private fun Wallet(
    uiState: WalletViewModel.UiState,
    onAddressCopyClicked: (WalletAddress) -> Unit,
    onTopUpClicked: () -> Unit,
    onWithdrawClicked: () -> Unit,
    onExchangeClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp)
    ) {
        Balance(
            totalBalance = uiState.totalBalance,
            address = uiState.address,
            onAddressCopyClicked = { onAddressCopyClicked(uiState.address) },
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
            uiState.wallets.forEach { item ->
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

@Composable
private fun Rewards(
    transactions: TransactionsUiState,
    filterOptions: WalletViewModel.FilterOptions,
    rewards: List<RewardsTileState>,
    onWithdrawClicked: () -> Unit,
    onOpened: () -> Unit,
    onDropdownMenuItemClicked: (WalletViewModel.FilterOptions) -> Unit,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit) {
        onOpened()
    }
    ScrollEndDetectLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        onScrollEndDetected = transactions.onListEndReaching,
    ) {
        items(rewards) {
            it.Content(modifier = Modifier.padding(bottom = 12.dp))
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .border(
                        width = 1.dp,
                        color = AppTheme.specificColorScheme.darkGrey.copy(alpha = 0.5f),
                        shape = AppTheme.shapes.medium,
                    )
                    .background(
                        color = AppTheme.specificColorScheme.white,
                        shape = AppTheme.shapes.medium,
                    )
                    .padding(12.dp)
                    .defaultTileRipple(onClick = onWithdrawClicked),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = AppTheme.specificIcons.withdraw.get(),
                    tint = AppTheme.specificColorScheme.white,
                    contentDescription = null,
                    modifier = Modifier
                        .background(
                            color = AppTheme.specificColorScheme.uiAccent,
                            shape = CircleShape
                        )
                        .padding(12.dp),
                )
                Text(
                    text = StringKey.WalletTitleWithdraw.textValue().get().text,
                    style = AppTheme.specificTypography.titleSmall,
                    color = AppTheme.specificColorScheme.textPrimary,
                    modifier = Modifier.padding(start = 20.dp),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = StringKey.WalletTitleHistory.textValue().get().text,
                    style = AppTheme.specificTypography.titleMedium,
                    color = AppTheme.specificColorScheme.textPrimary,
                )
                Box {
                    SimpleButtonGreyS(
                        modifier = Modifier.padding(vertical = 4.dp),
                        onClick = { isMenuExpanded = true }
                    ) {
                        SimpleButtonContent(
                            text = filterOptions.name.textValue(),
                            iconRight = AppTheme.specificIcons.arrowDropDown,
                            tint = AppTheme.specificColorScheme.black_80,
                            textColor = AppTheme.specificColorScheme.black_80,
                        )
                    }
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                    ) {
                        WalletViewModel.FilterOptions.values().forEach {
                            DropdownMenuItem(
                                onClick = {
                                    onDropdownMenuItemClicked(it)
                                    isMenuExpanded = false
                                },
                            ) {
                                Text(
                                    text = it.name,
                                    style = AppTheme.specificTypography.bodySmall,
                                )
                            }
                        }
                    }
                }
            }
        }
        transactionsItems(
            uiState = transactions,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}

@Composable
private fun Balance(
    totalBalance: TotalBalanceModel,
    address: WalletAddress,
    onAddressCopyClicked: () -> Unit,
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
                text = totalBalance.coin,
                style = AppTheme.specificTypography.titleLarge,
                color = AppTheme.specificColorScheme.textPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                textAlign = TextAlign.Center,
            )
            Text(
                text = totalBalance.fiat,
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
                onClick = onAddressCopyClicked,
            ) {
                SimpleButtonContent(
                    text = address.textValue(),
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
private fun SelectWalletDialog(
    wallets: List<CellTileState>,
) {
    SimpleBottomDialogUI(StringKey.WalletTitleSelectCurrency.textValue()) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(wallets) {
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
    title: TextValue,
    qr: Bitmap?,
    address: String,
    onAddressCopyClicked: () -> Unit,
) {
    SimpleBottomDialogUI(title) {
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
                onClick = onAddressCopyClicked,
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