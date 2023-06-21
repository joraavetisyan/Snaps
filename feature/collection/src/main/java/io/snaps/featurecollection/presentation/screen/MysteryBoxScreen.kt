package io.snaps.featurecollection.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.baseplayer.ui.VideoPlayer
import io.snaps.basewallet.ui.LimitedGasDialog
import io.snaps.basewallet.ui.LimitedGasDialogHandler
import io.snaps.basewallet.ui.TopUpDialog
import io.snaps.basewallet.ui.TransferTokensDialogHandler
import io.snaps.basewallet.ui.TransferTokensUi
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.MysteryBoxType
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.bottomsheetdialog.ModalBottomSheetCurrentStateListener
import io.snaps.coreuicompose.uikit.button.SimpleTwoLineButtonActionL
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBarConfig
import io.snaps.coreuicompose.uikit.listtile.HeaderTileState
import io.snaps.coreuicompose.uikit.status.FullScreenLoaderUi
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featurecollection.ScreenNavigator
import io.snaps.featurecollection.presentation.viewmodel.MysteryBoxViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MysteryBoxScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<MysteryBoxViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    val transferTokensState by viewModel.transferTokensState.collectAsState()
    val limitedGasState by viewModel.limitedGasState.collectAsState()

    val clipboardManager = LocalClipboardManager.current

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheetCurrentStateListener(
        sheetState = sheetState,
    ) {
        if (it) {
            viewModel.onBottomDialogHidden()
            viewModel.onLimitedGasDialogHidden()
            viewModel.onTransferTokensDialogHidden()
        }
    }

    viewModel.command.collectAsCommand {
        when (it) {
            is MysteryBoxViewModel.Command.BackToMyCollectionScreen -> if (it.data != null) {
                router.backToMyCollectionScreenWithResult(it.data)
            } else {
                router.backToMyCollectionScreen()
            }
            is MysteryBoxViewModel.Command.CopyText -> clipboardManager.setText(AnnotatedString(it.text))
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
            when (val dialog = uiState.bottomDialog) {
                // todo localize own strings
                is MysteryBoxViewModel.BottomDialog.TopUp -> TopUpDialog(
                    title = StringKey.WalletDialogTitleTopUp.textValue(dialog.title),
                    address = dialog.address,
                    qr = dialog.qr,
                    message = StringKey.PurchaseErrorNotEnoughBnb.textValue(),
                    onAddressCopyClicked = { viewModel.onAddressCopyClicked(dialog.address) },
                )
                null -> Unit
            }
            when (transferTokensState.bottomDialog) {
                TransferTokensDialogHandler.BottomDialog.TokensTransfer -> TransferTokensUi(
                    data = transferTokensState.state,
                )
                is TransferTokensDialogHandler.BottomDialog.TokensTransferSuccess,
                is TransferTokensDialogHandler.BottomDialog.TokensSellSuccess,
                is TransferTokensDialogHandler.BottomDialog.NftRepairSuccess,
                null -> Unit
            }
            when (val dialog = limitedGasState.bottomDialog) {
                is LimitedGasDialogHandler.BottomDialog.Refill -> LimitedGasDialog(
                    onRefillClick = dialog.onRefillClicked,
                )
                null -> Unit
            }
        },
    ) {
        MysteryBoxScreen(
            uiState = uiState,
            onBackClicked = router::back,
            onBuyWithBNBClicked = viewModel::onBuyWithBNBClicked,
        )
    }

    FullScreenLoaderUi(isLoading = uiState.isLoading || limitedGasState.isLoading)
    uiState.uri?.let {
        MysteryBoxVideo(
            uri = it,
            mysteryBoxType = uiState.type,
            onFinished = viewModel::onVideoFinished,
            onBackClicked = router::back,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MysteryBoxScreen(
    uiState: MysteryBoxViewModel.UiState,
    onBuyWithBNBClicked: () -> Unit,
    onBackClicked: () -> Boolean,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val localDensity = LocalDensity.current
    var buttonsHeight by remember { mutableStateOf(0.dp) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SimpleTopAppBar(
                title = when (uiState.type) {
                    MysteryBoxType.FirstTier -> StringKey.MysteryBoxTitle
                    MysteryBoxType.SecondTier -> StringKey.MysteryBoxTitlePrimeBox
                }.textValue(),
                scrollBehavior = scrollBehavior,
                navigationIcon = AppTheme.specificIcons.back to onBackClicked,
            )
        },
        floatingActionButton = {
            ActionButtons(
                uiState = uiState,
                onGloballyPositioned = {
                    buttonsHeight = with(localDensity) { it.size.height.toDp() }
                },
                onBuyWithBNBClicked = onBuyWithBNBClicked,
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .inset(insetAllExcludeTop())
                .verticalScroll(rememberScrollState())
                .padding(12.dp)
                .padding(bottom = buttonsHeight + 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            uiState.mysteryBoxInfoTile.Content(modifier = Modifier)
            Spacer(modifier = Modifier.height(16.dp))
            HeaderTileState.small(StringKey.MysteryBoxTitleChanceNftLoss.textValue()).Content(
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            uiState.items?.let {
                // todo add colors to palette
                val colors = when (uiState.type) {
                    MysteryBoxType.FirstTier -> mutableListOf(
                        Color(0xFF65AFF5), Color(0xFF7165F5), Color(0xFFAD65F5)
                    ).apply {
                        repeat(it.size - 3) { this.add(Color(0xFFF56E65)) }
                    }
                    MysteryBoxType.SecondTier -> mutableListOf(
                        Color(0xFFF56E65), Color(0xFFAD65F5), Color(0xFFF56E65),
                    ).apply {
                        repeat(it.size - 3) { this.add(Color(0xFFE3B40C)) }
                    }
                }
                it.forEachIndexed { index, items ->
                    items.Content(
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .background(color = colors[index], shape = AppTheme.shapes.medium),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MysteryBoxVideo(
    uri: String,
    mysteryBoxType: MysteryBoxType,
    onFinished: () -> Unit,
    onBackClicked: () -> Boolean,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Box(
        Modifier.fillMaxWidth()
    ) {
        VideoPlayer(
            localUri = uri,
            shouldPlay = true,
            isScrolling = false,
            progressPollFrequencyInMillis = 50L,
            onProgressChanged = {
                if (it == 1f) {
                    onFinished() // todo ExoPlayer.STATE_ENDED event is not getting triggered
                }
            },
            isRepeat = false,
        )
        SimpleTopAppBar(
            title = when (mysteryBoxType) {
                MysteryBoxType.FirstTier -> StringKey.MysteryBoxTitle
                MysteryBoxType.SecondTier -> StringKey.MysteryBoxTitlePrimeBox
            }.textValue(),
            scrollBehavior = scrollBehavior,
            navigationIcon = AppTheme.specificIcons.back to onBackClicked,
            colors = SimpleTopAppBarConfig.transparentColors()
        )
    }
}

@Composable
private fun ActionButtons(
    uiState: MysteryBoxViewModel.UiState,
    onBuyWithBNBClicked: () -> Unit,
    onGloballyPositioned: (LayoutCoordinates) -> Unit,
) {
    Column(
        modifier = Modifier
            .onGloballyPositioned { coordinates -> onGloballyPositioned(coordinates) }
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (uiState.isPurchasableWithBnb) {
            SimpleTwoLineButtonActionL(
                modifier = Modifier.fillMaxWidth(),
                onClick = onBuyWithBNBClicked,
                text = StringKey.PurchaseActionBuyWithBNB.textValue(),
                additionalText = StringKey.PurchaseFieldOff.textValue(uiState.costInCoin?.getFormatted().orEmpty()),
            )
        }
    }
}