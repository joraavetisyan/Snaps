package io.snaps.featurecollection.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.ui.MainHeader
import io.snaps.basewallet.ui.LimitedGasDialog
import io.snaps.basewallet.ui.LimitedGasDialogHandler
import io.snaps.basewallet.ui.TransferTokensDialogHandler
import io.snaps.basewallet.ui.TransferTokensSuccessData
import io.snaps.basewallet.ui.TransferTokensUi
import io.snaps.corecommon.R
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.corenavigation.base.openUrl
import io.snaps.corenavigation.base.resultFlow
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.LocalBottomNavigationHeight
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.bottomsheetdialog.ModalBottomSheetCurrentStateListener
import io.snaps.coreuicompose.uikit.bottomsheetdialog.SimpleBottomDialog
import io.snaps.coreuicompose.uikit.button.SimpleTwoLineButtonActionL
import io.snaps.coreuicompose.uikit.status.FullScreenLoaderUi
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
import io.snaps.featurecollection.ScreenNavigator
import io.snaps.featurecollection.presentation.viewmodel.MyCollectionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyCollectionScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<MyCollectionViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    val headerState by viewModel.headerUiState.collectAsState()
    val transferTokensState by viewModel.transferTokensState.collectAsState()
    val limitedGasState by viewModel.limitedGasState.collectAsState()
    val pullRefreshState = rememberPullRefreshState(uiState.isRefreshing, viewModel::onRefreshPulled)

    navHostController.resultFlow<TransferTokensSuccessData?>()?.collectAsCommand(action = viewModel::onTransactionResultReceived)

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    ModalBottomSheetCurrentStateListener(
        sheetState = sheetState,
    ) {
        viewModel.onBottomDialogStateChange(it)
        if (it) {
            viewModel.onLimitedGasDialogHidden()
            viewModel.onTransferTokensDialogHidden()
        }
    }

    viewModel.headerCommand.collectAsCommand {
        when (it) {
            MainHeaderHandler.Command.OpenProfileScreen -> router.toProfileScreen()
            MainHeaderHandler.Command.OpenWalletScreen -> router.toWalletScreen()
        }
    }

    viewModel.command.collectAsCommand {
        when (it) {
            is MyCollectionViewModel.Command.OpenRankSelectionScreen -> router.toRankSelectionScreen()
            is MyCollectionViewModel.Command.OpenNftDetailsScreen -> router.toUserNftDetailsScreen(it.args)
            is MyCollectionViewModel.Command.OpenWebViewScreen -> router.toWebView(it.url)
        }
    }

    viewModel.transferTokensCommand.collectAsCommand {
        when (it) {
            TransferTokensDialogHandler.Command.ShowBottomDialog -> coroutineScope.launch { sheetState.show() }
            TransferTokensDialogHandler.Command.HideBottomDialog -> coroutineScope.launch { sheetState.hide() }
        }
    }

    viewModel.limitedGasCommand.collectAsCommand {
        when (it) {
            LimitedGasDialogHandler.Command.ShowBottomDialog -> coroutineScope.launch { sheetState.show() }
            LimitedGasDialogHandler.Command.HideBottomDialog -> coroutineScope.launch { sheetState.hide() }
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            when (val dialog = transferTokensState.bottomDialog) {
                TransferTokensDialogHandler.BottomDialog.TokensTransfer -> TransferTokensUi(
                    data = transferTokensState.state,
                )
                is TransferTokensDialogHandler.BottomDialog.TokensTransferSuccess -> SimpleBottomDialog(
                    image = R.drawable.img_guy_hands_up.imageValue(),
                    title = StringKey.MyCollectionDialogRepairSuccessTitle.textValue(),
                    buttonText = StringKey.MyCollectionDialogRepairSuccessAction.textValue(),
                    onClick = {
                        coroutineScope.launch { sheetState.hide() }
                        context.openUrl(dialog.bscScanLink)
                    },
                )
                is TransferTokensDialogHandler.BottomDialog.NftRepairSuccess -> SimpleBottomDialog(
                    image = R.drawable.img_guy_hands_up.imageValue(),
                    title = StringKey.MyCollectionDialogWithBnbSuccessTitle.textValue(),
                    text = StringKey.MyCollectionDialogWithBnbSuccessMessage.textValue(),
                    buttonText = StringKey.MyCollectionDialogWithBnbSuccessAction.textValue(),
                    onClick = {
                        coroutineScope.launch { sheetState.hide() }
                        context.openUrl(dialog.bscScanLink)
                    },
                )
                is TransferTokensDialogHandler.BottomDialog.TokensSellSuccess,
                null -> Unit
            }
            when (val dialog = limitedGasState.bottomDialog) {
                is LimitedGasDialogHandler.BottomDialog.Refill -> LimitedGasDialog(
                    onRefillClick = dialog.onRefillClicked,
                )
                null -> Unit
            }
        }
    ) {
        MyCollectionScreen(
            uiState = uiState,
            headerState = headerState,
            pullRefreshState = pullRefreshState,
            onRepairAllNftClicked = viewModel::onRepairAllNftClicked,
        )
    }

    FullScreenLoaderUi(isLoading = uiState.isLoading || limitedGasState.isLoading)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
private fun MyCollectionScreen(
    uiState: MyCollectionViewModel.UiState,
    headerState: MainHeaderHandler.UiState,
    pullRefreshState: PullRefreshState,
    onRepairAllNftClicked: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val localDensity = LocalDensity.current
    var buttonHeight by remember { mutableStateOf(0.dp) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {},
        floatingActionButton = {
            if (uiState.isAllNftRepairButtonVisible) {
                SimpleTwoLineButtonActionL(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = LocalBottomNavigationHeight.current)
                        .onGloballyPositioned {
                            buttonHeight = with(localDensity) { it.size.height.toDp() + 24.dp }
                        },
                    onClick = onRepairAllNftClicked,
                    text = StringKey.MyCollectionActionRepairAllGlasses.textValue(),
                    additionalText = uiState.nftsRepairCost.getFormatted().textValue(),
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .inset(insetAllExcludeTop())
                .padding(bottom = buttonHeight),
        ) {
            MainHeader(state = headerState.value)
            Header()
            Box(
                modifier = Modifier.pullRefresh(pullRefreshState),
            ) {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(
                        top = 12.dp,
                        start = 12.dp,
                        end = 12.dp,
                        bottom = LocalBottomNavigationHeight.current + 12.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(uiState.nft) {
                        it.Content(modifier = Modifier)
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
}

@Composable
private fun Header() {
    Column(
        Modifier.padding(12.dp)
    ) {
        Text(text = LocalStringHolder.current(StringKey.MyCollectionTitle), style = AppTheme.specificTypography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = LocalStringHolder.current(StringKey.MyCollectionMessage),
            style = AppTheme.specificTypography.titleSmall,
            color = AppTheme.specificColorScheme.textSecondary,
        )
    }
}