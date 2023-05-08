package io.snaps.featurewallet.screen

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.basewallet.domain.TotalBalanceModel
import io.snaps.corecommon.R
import io.snaps.corecommon.container.IconValue
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.WalletAddress
import io.snaps.corecommon.strings.StringKey
import io.snaps.corecommon.strings.addressEllipsized
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.doOnClick
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.bottomsheetdialog.FootnoteBottomDialog
import io.snaps.coreuicompose.uikit.bottomsheetdialog.FootnoteBottomDialogItem
import io.snaps.coreuicompose.uikit.bottomsheetdialog.SimpleBottomDialogUI
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonGreyM
import io.snaps.coreuicompose.uikit.button.SimpleButtonGreyS
import io.snaps.coreuicompose.uikit.button.SimpleButtonOutlineL
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.other.TitleSlider
import io.snaps.coreuicompose.uikit.scroll.ScrollEndDetectLazyColumn
import io.snaps.coreuicompose.uikit.status.FootnoteUi
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

    val clipboardManager = LocalClipboardManager.current

    val uiState by viewModel.uiState.collectAsState()
    val pullRefreshState = rememberPullRefreshState(uiState.isRefreshing, { viewModel.refresh() })

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    val coroutineScope = rememberCoroutineScope()

    viewModel.command.collectAsCommand {
        when (it) {
            WalletViewModel.Command.ShowBottomDialog -> coroutineScope.launch { sheetState.show() }
            WalletViewModel.Command.HideBottomDialog -> coroutineScope.launch { sheetState.hide() }
            is WalletViewModel.Command.OpenWithdrawScreen -> router.toWithdrawScreen(it.wallet)
            is WalletViewModel.Command.OpenExchangeScreen -> router.toExchangeScreen(it.wallet)
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
            when (val dialog = uiState.bottomDialog) {
                is WalletViewModel.BottomDialog.SelectWallet -> SelectWalletDialog(
                    wallets = dialog.wallets,
                )
                is WalletViewModel.BottomDialog.TopUp -> TopUpDialog(
                    title = StringKey.WalletDialogTitleTopUp.textValue(dialog.title),
                    address = dialog.address,
                    qr = dialog.qr,
                    onAddressCopyClicked = { onAddressCopyClicked(dialog.address) },
                )
                WalletViewModel.BottomDialog.RewardsFootnote -> FootnoteBottomDialog(
                    FootnoteBottomDialogItem(
                        image = ImageValue.ResImage(R.drawable.img_guy_eating),
                        title = StringKey.RewardsDialogTitleFootnote1.textValue(),
                        text = StringKey.RewardsDialogMessageFootnote1.textValue(),
                    ),
                    FootnoteBottomDialogItem(
                        image = ImageValue.ResImage(R.drawable.img_guy_glad),
                        title = StringKey.RewardsDialogTitleFootnote2.textValue(),
                        text = StringKey.RewardsDialogMessageFootnote2.textValue(),
                    ),
                )
            }
        },
    ) {
        WalletScreen(
            uiState = uiState,
            pullRefreshState = pullRefreshState,
            onBackClicked = router::back,
            onAddressCopyClicked = ::onAddressCopyClicked,
            onTopUpClicked = viewModel::onTopUpClicked,
            onWithdrawClicked = viewModel::onWithdrawClicked,
            onRewardsWithdrawClicked = viewModel::onRewardsWithdrawClicked,
            onExchangeClicked = viewModel::onExchangeClicked,
            onRewardsOpened = viewModel::onRewardsOpened,
            onRewardsFootnoteClick = viewModel::onRewardsFootnoteClick,
            onDropdownMenuItemClicked = viewModel::onDropdownMenuItemClicked,
            onPageSelected = viewModel::onPageSelected,
        )
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class,
)
@Composable
private fun WalletScreen(
    uiState: WalletViewModel.UiState,
    pullRefreshState: PullRefreshState,
    onBackClicked: () -> Boolean,
    onAddressCopyClicked: (WalletAddress) -> Unit,
    onTopUpClicked: () -> Unit,
    onWithdrawClicked: () -> Unit,
    onRewardsWithdrawClicked: () -> Unit,
    onExchangeClicked: () -> Unit,
    onRewardsOpened: () -> Unit,
    onRewardsFootnoteClick: () -> Unit,
    onDropdownMenuItemClicked: (WalletViewModel.FilterOptions) -> Unit,
    onPageSelected: (Int) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onPageSelected(page)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = AppTheme.specificColorScheme.uiContentBg,
        topBar = {
            SimpleTopAppBar(
                title = {
                    TitleSlider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        items = WalletViewModel.Screen.values().map { it.label },
                        selectedItemIndex = pagerState.currentPage,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(it)
                            }
                        },
                    )
                },
                navigationIcon = AppTheme.specificIcons.back to onBackClicked,
                scrollBehavior = scrollBehavior,
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .inset(insetAllExcludeTop())
                .pullRefresh(pullRefreshState),
        ) {
            HorizontalPager(
                pageCount = 2,
                state = pagerState,
            ) {
                when (it) {
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
                        onRewardsFootnoteClick = onRewardsFootnoteClick,
                        onDropdownMenuItemClicked = onDropdownMenuItemClicked,
                        isWithdrawVisible = uiState.isRewardsWithdrawVisible,
                    )
                }
            }
            PullRefreshIndicator(
                uiState.isRefreshing,
                pullRefreshState,
                Modifier.align(Alignment.TopCenter)
            )
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
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = StringKey.WalletTitle.textValue().get(),
            style = AppTheme.specificTypography.headlineMedium,
            modifier = Modifier.padding(16.dp),
        )
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
                title = StringKey.WalletActionTopUp.textValue().get().text,
                image = AppTheme.specificIcons.topUp,
                onClick = onTopUpClicked,
            )
            OperationType(
                title = StringKey.WalletActionWithdraw.textValue().get().text,
                image = AppTheme.specificIcons.withdraw,
                onClick = onWithdrawClicked,
            )
            OperationType(
                title = StringKey.WalletActionExchange.textValue().get().text,
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
                        .shadow(elevation = 16.dp, shape = AppTheme.shapes.medium)
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
    isWithdrawVisible: Boolean,
    onOpened: () -> Unit,
    onRewardsFootnoteClick: () -> Unit,
    onDropdownMenuItemClicked: (WalletViewModel.FilterOptions) -> Unit,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit) {
        onOpened()
    }
    ScrollEndDetectLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        onScrollEndDetected = transactions.onListEndReaching,
    ) {
        item {
            FootnoteUi(
                title = StringKey.RewardsTitleFootnote.textValue(),
                description = StringKey.RewardsMessageFootnote.textValue(),
                action = StringKey.ActionHowItWorks.textValue(),
                onClick = onRewardsFootnoteClick,
                padding = 0.dp,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(rewards) {
            it.Content(modifier = Modifier.padding(bottom = 12.dp))
        }
        item {
            if (isWithdrawVisible) {
                SimpleButtonOutlineL(
                    onClick = onWithdrawClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                ) {
                    SimpleButtonContent(
                        text = StringKey.RewardsActionClaim.textValue(),
                        contentLeft = {
                            Icon(
                                painter = AppTheme.specificIcons.withdraw.get(),
                                tint = AppTheme.specificColorScheme.white,
                                contentDescription = null,
                                modifier = Modifier
                                    .background(
                                        color = AppTheme.specificColorScheme.uiAccent,
                                        shape = CircleShape,
                                    )
                                    .padding(4.dp),
                            )
                        },
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = StringKey.RewardsTitleHistory.textValue().get().text,
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
                            iconTint = AppTheme.specificColorScheme.black_80,
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
                                    text = it.name, // todo localize filter name
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
                    text = address.addressEllipsized.textValue(),
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
    SimpleBottomDialogUI(header = StringKey.WalletTitleSelectCurrency.textValue()) {
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
    address: WalletAddress,
    onAddressCopyClicked: () -> Unit,
) {
    SimpleBottomDialogUI(header = title) {
        item {
            qr?.let {
                Image(
                    modifier = Modifier.size(164.dp),
                    bitmap = it.asImageBitmap(),
                    contentScale = ContentScale.FillWidth,
                    contentDescription = null,
                )
            }
            SimpleButtonGreyM(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                onClick = onAddressCopyClicked,
            ) {
                SimpleButtonContent(
                    text = address.addressEllipsized.textValue(),
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