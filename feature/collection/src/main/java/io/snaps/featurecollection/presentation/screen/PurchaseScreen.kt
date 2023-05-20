package io.snaps.featurecollection.presentation.screen

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.basenft.ui.rankCostToString
import io.snaps.baseprofile.ui.ValueWidget
import io.snaps.basewallet.ui.LimitedGasDialog
import io.snaps.basewallet.ui.LimitedGasDialogHandler
import io.snaps.basewallet.ui.TransferTokensDialogHandler
import io.snaps.basewallet.ui.TransferTokensUi
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.toPercentageFormat
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.strings.StringKey
import io.snaps.corenavigation.base.openUrl
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.bottomsheetdialog.ModalBottomSheetCurrentStateListener
import io.snaps.coreuicompose.uikit.bottomsheetdialog.ModalBottomSheetTargetStateListener
import io.snaps.coreuicompose.uikit.bottomsheetdialog.SimpleBottomDialog
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonDefaultM
import io.snaps.coreuicompose.uikit.button.SimpleTwoLineButtonActionL
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuicompose.uikit.other.SimpleCard
import io.snaps.coreuicompose.uikit.status.FullScreenLoaderUi
import io.snaps.coreuicompose.uikit.status.InfoBlock
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featurecollection.ScreenNavigator
import io.snaps.featurecollection.presentation.viewmodel.PurchaseViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PurchaseScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<PurchaseViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val transferTokensState by viewModel.transferTokensState.collectAsState()
    val limitedGasState by viewModel.limitedGasState.collectAsState()
    val context = LocalContext.current

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheetTargetStateListener(
        sheetState = sheetState,
        onStateToChange = {
            if (it) {
                viewModel.onLimitedGasDialogHidden()
                viewModel.onTransferTokensDialogHidden()
            }
        },
    )

    viewModel.command.collectAsCommand {
        when (it) {
            PurchaseViewModel.Command.BackToMyCollectionScreen -> router.backToMyCollectionScreen()
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
                    title = StringKey.PurchaseDialogWithBnbSuccessTitle.textValue(),
                    text = StringKey.PurchaseDialogWithBnbSuccessMessage.textValue(),
                    buttonText = StringKey.PurchaseDialogWithBnbSuccessAction.textValue(),
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
        },
    ) {
        PurchaseScreen(
            uiState = uiState,
            onBackClicked = router::back,
            onBuyWithGooglePlayClicked = { viewModel.onBuyWithGooglePlayClicked(context as Activity) },
            onBuyWithBNBClicked = viewModel::onBuyWithBNBClicked,
            onFreeClicked = viewModel::onFreeClicked,
        )
    }

    FullScreenLoaderUi(isLoading = uiState.isLoading || limitedGasState.isLoading)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PurchaseScreen(
    uiState: PurchaseViewModel.UiState,
    onBuyWithGooglePlayClicked: () -> Unit,
    onBuyWithBNBClicked: () -> Unit,
    onFreeClicked: () -> Unit,
    onBackClicked: () -> Boolean,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val localDensity = LocalDensity.current
    var buttonsHeight by remember { mutableStateOf(0.dp) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SimpleTopAppBar(
                title = StringKey.PurchaseTitle.textValue(),
                scrollBehavior = scrollBehavior,
                navigationIcon = AppTheme.specificIcons.back to onBackClicked,
            )
        },
        floatingActionButton = {
            if (uiState.isPurchasable) {
                ActionButtons(
                    uiState = uiState,
                    onGloballyPositioned = {
                        buttonsHeight = with(localDensity) { it.size.height.toDp() }
                    },
                    onBuyWithBNBClicked = onBuyWithBNBClicked,
                    onFreeClicked = onFreeClicked,
                    onBuyWithGooglePlayClicked = onBuyWithGooglePlayClicked,
                    isFreeButtonVisible = uiState.isPurchasableForFree,
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .inset(insetAllExcludeTop())
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
                .padding(bottom = buttonsHeight + 24.dp),
        ) {
            if (uiState.isPurchasable) {
                NftInfoBlock(
                    nftType = uiState.nftType,
                    nftImage = uiState.nftImage,
                    cost = uiState.cost.rankCostToString(),
                )
            } else {
                UnavailableNftInfoBlock(
                    nftType = uiState.nftType,
                    nftImage = uiState.nftImage,
                    prevNftImage = uiState.prevNftImage,
                )
            }
            CardBlock(
                title = StringKey.PurchaseTitleDailyReward.textValue(uiState.dailyReward.getFormatted()),
                description = StringKey.PurchaseDescriptionDailyReward.textValue(),
                message = StringKey.PurchaseMessageDailyReward.textValue(),
            )
            Spacer(modifier = Modifier.height(12.dp))
            CardBlock(
                title = StringKey.PurchaseTitleDailyUnlock.textValue(
                    uiState.dailyUnlock.toPercentageFormat()
                ),
                description = StringKey.PurchaseDescriptionDailyUnlock.textValue(
                    uiState.dailyUnlock.toPercentageFormat()
                ),
                message = StringKey.PurchaseMessageDailyUnlock.textValue(),
            )
            Spacer(modifier = Modifier.height(12.dp))
            CardBlock(
                title = StringKey.PurchaseTitleDailyCosts.textValue(),
                description = StringKey.PurchaseMessageDailyCosts.textValue(),
            )
        }
    }
}

@Composable
fun CardBlock(
    title: TextValue,
    description: TextValue,
    message: TextValue? = null,
) {
    SimpleCard {
        Text(
            text = title.get(),
            style = AppTheme.specificTypography.titleSmall,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(top = 12.dp),
        )
        Text(
            text = description.get(),
            style = AppTheme.specificTypography.bodyMedium,
            color = AppTheme.specificColorScheme.textSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(top = 4.dp, bottom = 12.dp)
        )
        message?.let {
            InfoBlock(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
                message = it,
            )
        }
    }
}

@Composable
private fun ActionButtons(
    uiState: PurchaseViewModel.UiState,
    isFreeButtonVisible: Boolean,
    onFreeClicked: () -> Unit,
    onBuyWithGooglePlayClicked: () -> Unit,
    onBuyWithBNBClicked: () -> Unit,
    onGloballyPositioned: (LayoutCoordinates) -> Unit,
) {
    Column(
        modifier = Modifier
            .onGloballyPositioned { coordinates -> onGloballyPositioned(coordinates) }
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (uiState.nftType == NftType.Free) {
            if (isFreeButtonVisible) {
                SimpleButtonActionM(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onFreeClicked,
                ) {
                    SimpleButtonContent(text = StringKey.PurchaseActionFree.textValue())
                }
            }
        } else {
            SimpleButtonDefaultM(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 16.dp, shape = CircleShape),
                onClick = onBuyWithGooglePlayClicked,
            ) {
                SimpleButtonContent(
                    text = StringKey.PurchaseActionBuyInStore.textValue(),
                )
            }
            if (uiState.isPurchasableWithBnb) {
                SimpleTwoLineButtonActionL(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onBuyWithBNBClicked,
                    text = StringKey.PurchaseActionBuyWithBNB.textValue(),
                    additionalText = StringKey.PurchaseFieldOff.textValue(),
                )
            }
        }
    }
}

@Composable
private fun NftInfoBlock(
    nftType: NftType,
    nftImage: ImageValue,
    cost: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Image(
            painter = nftImage.get(),
            contentDescription = null,
            modifier = Modifier.size(100.dp),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = nftType.name,
                style = AppTheme.specificTypography.headlineSmall,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = StringKey.PurchaseTitlePrice.textValue().get(),
                    style = AppTheme.specificTypography.bodyMedium,
                    color = AppTheme.specificColorScheme.textSecondary,
                )
                ValueWidget(R.drawable.img_coin_silver.imageValue() to cost)
            }
        }
    }
}

@Composable
private fun UnavailableNftInfoBlock(
    nftType: NftType,
    nftImage: ImageValue,
    prevNftImage: ImageValue,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NftImage(image = nftImage)
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = AppTheme.specificColorScheme.uiAccent.copy(0.1f),
                    shape = CircleShape,
                )
                .padding(8.dp),
        ) {
            Icon(
                painter = AppTheme.specificIcons.trophy.get(),
                contentDescription = null,
                tint = AppTheme.specificColorScheme.uiAccent,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.TopCenter),
            )
            Text(
                text = StringKey.PurchaseFieldLevel.textValue("10").get(),
                color = AppTheme.specificColorScheme.uiAccent,
                style = AppTheme.specificTypography.labelMedium,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
        NftImage(image = prevNftImage)
    }
    Text(
        text = StringKey.PurchaseTitleRank.textValue(nftType.name).get(),
        style = AppTheme.specificTypography.headlineSmall,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
    )
    Text(
        text = StringKey.PurchaseTitleNotAvailable.textValue(nftType.name).get(),
        style = AppTheme.specificTypography.bodyLarge,
        color = AppTheme.specificColorScheme.textSecondary,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
private fun RowScope.NftImage(
    image: ImageValue,
) {
    Image(
        painter = image.get(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(100.dp)
            .weight(1f),
    )
}