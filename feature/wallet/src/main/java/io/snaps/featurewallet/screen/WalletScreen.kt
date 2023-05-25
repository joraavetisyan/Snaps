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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.basewallet.ui.TransferTokensDialogHandler
import io.snaps.basewallet.ui.TransferTokensSuccessData
import io.snaps.corecommon.R
import io.snaps.corecommon.container.IconValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.CoinValue
import io.snaps.corecommon.model.CryptoAddress
import io.snaps.corecommon.strings.StringKey
import io.snaps.corecommon.strings.addressEllipsized
import io.snaps.corenavigation.base.openUrl
import io.snaps.corenavigation.base.resultFlow
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.doOnClick
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.bottomsheetdialog.FootnoteBottomDialog
import io.snaps.coreuicompose.uikit.bottomsheetdialog.FootnoteBottomDialogItem
import io.snaps.coreuicompose.uikit.bottomsheetdialog.ModalBottomSheetTargetStateListener
import io.snaps.coreuicompose.uikit.bottomsheetdialog.SimpleBottomDialog
import io.snaps.coreuicompose.uikit.bottomsheetdialog.SimpleBottomDialogUI
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionS
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonGreyM
import io.snaps.coreuicompose.uikit.button.SimpleButtonGreyS
import io.snaps.coreuicompose.uikit.button.SimpleButtonOutlineM
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuicompose.uikit.input.SimpleTextField
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.other.TitleSlider
import io.snaps.coreuicompose.uikit.scroll.ScrollEndDetectLazyColumn
import io.snaps.coreuicompose.uikit.status.FootnoteUi
import io.snaps.coreuicompose.uikit.status.FullScreenLoaderUi
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featurewallet.ScreenNavigator
import io.snaps.featurewallet.viewmodel.WalletViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun WalletScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<WalletViewModel>()

    navHostController.resultFlow<TransferTokensSuccessData?>()?.collectAsCommand(action = viewModel::onTransactionResultReceived)

    val clipboardManager = LocalClipboardManager.current

    val uiState by viewModel.uiState.collectAsState()
    val transferTokensState by viewModel.transferTokensState.collectAsState()
    val pullRefreshState = rememberPullRefreshState(uiState.isRefreshing, { viewModel.onRefreshPulled() })

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = sheetState.currentValue) {
        if (sheetState.currentValue == ModalBottomSheetValue.Hidden
            && uiState.bottomDialog == WalletViewModel.BottomDialog.RewardsWithdraw
        ) {
            focusRequester.freeFocus()
            keyboardController?.hide()
        }
    }

    ModalBottomSheetTargetStateListener(
        sheetState = sheetState,
        onStateToChange = {
            if (it) {
                viewModel.onBottomDialogHidden()
                viewModel.onTransferTokensDialogHidden()
            }
        },
    )

    viewModel.command.collectAsCommand {
        when (it) {
            WalletViewModel.Command.ShowBottomDialog -> coroutineScope.launch { sheetState.show() }
            WalletViewModel.Command.HideBottomDialog -> coroutineScope.launch { sheetState.hide() }
            is WalletViewModel.Command.OpenWithdrawScreen -> router.toWithdrawScreen(coinType = it.coinType)
            is WalletViewModel.Command.OpenExchangeScreen -> router.toExchangeScreen(coinType = it.coinType)
            WalletViewModel.Command.OpenWithdrawSnapsScreen -> router.toWithdrawSnapsScreen()
            is WalletViewModel.Command.CopyText -> clipboardManager.setText(AnnotatedString(it.text))
            is WalletViewModel.Command.OpenLink -> context.openUrl(it.link)
        }
    }
    viewModel.transferTokensCommand.collectAsCommand {
        when (it) {
            TransferTokensDialogHandler.Command.ShowBottomDialog -> coroutineScope.launch { sheetState.show() }
            TransferTokensDialogHandler.Command.HideBottomDialog -> coroutineScope.launch { sheetState.hide() }
        }
    }

    BackHandler(enabled = sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
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
                    onAddressCopyClicked = { viewModel.onAddressCopyClicked(dialog.address) },
                )
                WalletViewModel.BottomDialog.RewardsFootnote -> FootnoteBottomDialog(
                    FootnoteBottomDialogItem(
                        image = R.drawable.img_guy_eating.imageValue(),
                        title = StringKey.RewardsDialogTitleFootnote1.textValue(),
                        text = StringKey.RewardsDialogMessageFootnote1.textValue(),
                    ),
                    FootnoteBottomDialogItem(
                        image = R.drawable.img_guy_glad.imageValue(),
                        title = StringKey.RewardsDialogTitleFootnote2.textValue(),
                        text = StringKey.RewardsDialogMessageFootnote2.textValue(),
                    ),
                )
                WalletViewModel.BottomDialog.RewardsWithdraw -> RewardsClaimDialog(
                    amountValue = uiState.claimAmountValue,
                    availableTokens = uiState.availableTokens,
                    isConfirmButtonEnabled = uiState.isConfirmClaimEnabled,
                    focusRequester = focusRequester,
                    onAmountValueChanged = viewModel::onAmountToClaimValueChanged,
                    onConfirmClicked = viewModel::onConfirmClaimClicked,
                    onMaxButtonClicked = viewModel::onRewardsMaxButtonClicked,
                )
                WalletViewModel.BottomDialog.RepairNft -> SimpleBottomDialog(
                    image = R.drawable.img_guy_sad.imageValue(),
                    title = StringKey.RewardsDialogRepairNftTitle.textValue(),
                    text = StringKey.RewardsDialogRepairNftText.textValue(),
                    buttonText = StringKey.RewardsDialogRepairNftAction.textValue(),
                    onClick = {
                        coroutineScope.launch { sheetState.hide() }
                        router.toMyCollectionScreen()
                    }
                )
                null -> Unit
            }
            when (val dialog = transferTokensState.bottomDialog) {
                is TransferTokensDialogHandler.BottomDialog.TokensTransferSuccess -> SimpleBottomDialog(
                    image = R.drawable.img_guy_hands_up.imageValue(),
                    title = StringKey.WithdrawDialogWithdrawSuccessTitle.textValue(),
                    text = StringKey.WithdrawDialogWithdrawSuccessMessage.textValue(
                        dialog.sent?.getFormatted().orEmpty(),
                        dialog.to.orEmpty()
                    ),
                    buttonText = StringKey.WithdrawDialogWithdrawSuccessAction.textValue(),
                    onClick = {
                        coroutineScope.launch { sheetState.hide() }
                        context.openUrl(dialog.bscScanLink)
                    },
                )
                is TransferTokensDialogHandler.BottomDialog.TokensSellSuccess -> SimpleBottomDialog(
                    image = R.drawable.img_guy_hands_up.imageValue(),
                    title = StringKey.MessageSuccess.textValue(),
                    text = StringKey.WalletDialogSellSnapsMessage.textValue(),
                    buttonText = StringKey.WithdrawDialogWithdrawSuccessAction.textValue(),
                    onClick = {
                        coroutineScope.launch { sheetState.hide() }
                        context.openUrl(dialog.bscScanLink)
                    },
                )
                TransferTokensDialogHandler.BottomDialog.TokensTransfer,
                is TransferTokensDialogHandler.BottomDialog.NftRepairSuccess,
                null -> Unit
            }
        },
    ) {
        WalletScreen(
            uiState = uiState,
            pullRefreshState = pullRefreshState,
            onBackClicked = router::back,
            onAddressCopyClicked = viewModel::onAddressCopyClicked,
            onSellSnapsClicked = viewModel::onSellSnapsClicked,
            onTopUpClicked = viewModel::onTopUpClicked,
            onWithdrawClicked = viewModel::onWithdrawClicked,
            onClaimClicked = viewModel::onRewardsClaimClicked,
            onExchangeClicked = viewModel::onExchangeClicked,
            onRewardsOpened = viewModel::onRewardsOpened,
            onRewardsFootnoteClick = viewModel::onRewardsFootnoteClick,
            onDropdownMenuItemClicked = viewModel::onDropdownMenuItemClicked,
            onPageSelected = viewModel::onPageSelected,
        )
    }
    FullScreenLoaderUi(isLoading = uiState.isLoading)
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
    onAddressCopyClicked: (CryptoAddress) -> Unit,
    onSellSnapsClicked: () -> Unit,
    onTopUpClicked: () -> Unit,
    onWithdrawClicked: () -> Unit,
    onClaimClicked: () -> Unit,
    onExchangeClicked: () -> Unit,
    onRewardsOpened: () -> Unit,
    onRewardsFootnoteClick: () -> Unit,
    onDropdownMenuItemClicked: (WalletViewModel.Filter) -> Unit,
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
                        onSellSnapsClicked = onSellSnapsClicked,
                    )
                    1 -> Rewards(
                        transactions = uiState.transactions,
                        filter = uiState.filter,
                        rewards = uiState.rewards,
                        onClaimClicked = onClaimClicked,
                        onOpened = onRewardsOpened,
                        onRewardsFootnoteClick = onRewardsFootnoteClick,
                        onDropdownMenuItemClicked = onDropdownMenuItemClicked,
                    )
                }
            }
            PullRefreshIndicator(
                refreshing = uiState.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }
}

@Composable
private fun Wallet(
    uiState: WalletViewModel.UiState,
    onAddressCopyClicked: (CryptoAddress) -> Unit,
    onSellSnapsClicked: () -> Unit,
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
        uiState.payoutStatusState?.let {
            PayoutStatus(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                data = it,
            )
        }
        Balance(
            uiState = uiState,
            onAddressCopyClicked = { onAddressCopyClicked(uiState.address) },
            onSellSnapsClicked = onSellSnapsClicked,
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
    filter: WalletViewModel.Filter,
    rewards: List<RewardsTileState>,
    onClaimClicked: () -> Unit,
    onOpened: () -> Unit,
    onRewardsFootnoteClick: () -> Unit,
    onDropdownMenuItemClicked: (WalletViewModel.Filter) -> Unit,
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
            SimpleButtonOutlineM(
                onClick = onClaimClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
            ) {
                SimpleButtonContent(
                    text = StringKey.RewardsActionClaim.textValue(),
                    iconLeft = AppTheme.specificIcons.sendCircled,
                )
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
                            text = filter.name.textValue(),
                            iconRight = AppTheme.specificIcons.arrowDropDown,
                            iconTint = AppTheme.specificColorScheme.black_80,
                            textColor = AppTheme.specificColorScheme.black_80,
                        )
                    }
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                    ) {
                        WalletViewModel.Filter.values().forEach {
                            DropdownMenuItem(
                                onClick = {
                                    onDropdownMenuItemClicked(it)
                                    isMenuExpanded = false
                                },
                            ) {
                                Text(
                                    text = it.label.get(),
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
    uiState: WalletViewModel.UiState,
    onAddressCopyClicked: () -> Unit,
    onSellSnapsClicked: () -> Unit,
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
                text = uiState.totalBalance.fiat.getFormatted(),
                style = AppTheme.specificTypography.titleLarge,
                color = AppTheme.specificColorScheme.textPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                textAlign = TextAlign.Center,
            )
            SimpleButtonGreyM(
                modifier = Modifier.fillMaxWidth(),
                onClick = onAddressCopyClicked,
            ) {
                SimpleButtonContent(
                    text = uiState.address.addressEllipsized.textValue(),
                    iconRight = AppTheme.specificIcons.copy,
                )
            }
            if (uiState.isSnapsSellEnabled) {
                SimpleButtonActionM(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    onClick = onSellSnapsClicked,
                ) {
                    SimpleButtonContent(
                        text = "Sell SNAPS".textValue(),
                        iconLeft = AppTheme.specificIcons.snpToken,
                    )
                }
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
    address: CryptoAddress,
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

@Composable
private fun RewardsClaimDialog(
    amountValue: String,
    availableTokens: CoinValue,
    isConfirmButtonEnabled: Boolean,
    focusRequester: FocusRequester,
    onAmountValueChanged: (String) -> Unit,
    onMaxButtonClicked: () -> Unit,
    onConfirmClicked: () -> Unit,
) {
    SimpleBottomDialogUI(header = StringKey.RewardsDialogTitleClaim.textValue()) {
        item {
            SimpleTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .focusRequester(focusRequester),
                onValueChange = onAmountValueChanged,
                value = amountValue,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                placeholder = {
                    Text(
                        text = StringKey.RewardsDialogHintClaim.textValue().get(),
                        style = AppTheme.specificTypography.titleSmall,
                    )
                },
                trailingIcon = {
                    SimpleButtonActionS(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        onClick = onMaxButtonClicked,
                    ) {
                        SimpleButtonContent(
                            text = StringKey.RewardsDialogActionMax.textValue(),
                        )
                    }
                },
                maxLines = 1,
            )
            Text(
                text = StringKey.RewardsDialogFieldAvailable.textValue(availableTokens.getFormatted()).get(),
                style = AppTheme.specificTypography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                textAlign = TextAlign.End,
            )
            SimpleButtonActionM(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 20.dp),
                onClick = onConfirmClicked,
                enabled = isConfirmButtonEnabled,
            ) {
                SimpleButtonContent(
                    text = StringKey.RewardsDialogActionClaim.textValue(),
                )
            }
        }
    }
}