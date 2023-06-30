package io.snaps.featurecollection.presentation.viewmodel

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basenft.data.NftRepository
import io.snaps.basenft.domain.MysteryBoxModel
import io.snaps.basesources.NotificationsSource
import io.snaps.basesources.featuretoggle.Feature
import io.snaps.basesources.featuretoggle.FeatureToggle
import io.snaps.basewallet.data.WalletRepository
import io.snaps.basewallet.domain.NftMintSummary
import io.snaps.basewallet.ui.LimitedGasDialogHandler
import io.snaps.basewallet.ui.TransferTokensDialogHandler
import io.snaps.basewallet.ui.TransferTokensState
import io.snaps.basewallet.ui.TransferTokensSuccessData
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.CoinBNB
import io.snaps.corecommon.model.CoinValue
import io.snaps.corecommon.model.CryptoAddress
import io.snaps.corecommon.model.MysteryBoxType
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.barcode.BarcodeManager
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.LeftPart
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreui.FileManager
import io.snaps.featurecollection.domain.BalanceInSync
import io.snaps.featurecollection.domain.MyCollectionInteractor
import io.snaps.featurecollection.domain.NoEnoughBnbToMint
import io.snaps.featurecollection.presentation.screen.MysteryBoxInfoTileState
import io.snaps.featurecollection.presentation.toMysteryBoxInfoTileState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MysteryBoxViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    transferTokensDialogHandler: TransferTokensDialogHandler,
    limitedGasDialogHandler: LimitedGasDialogHandler,
    private val featureToggle: FeatureToggle,
    @Bridged private val nftRepository: NftRepository,
    @Bridged private val walletRepository: WalletRepository,
    private val action: Action,
    private val notificationsSource: NotificationsSource,
    private val interactor: MyCollectionInteractor,
    private val barcodeManager: BarcodeManager,
    private val fileManager: FileManager,
) : SimpleViewModel(),
    TransferTokensDialogHandler by transferTokensDialogHandler,
    LimitedGasDialogHandler by limitedGasDialogHandler {

    private val args = savedStateHandle.requireArgs<AppRoute.MysteryBox.Args>()

    private val _uiState = MutableStateFlow(
        initialUiState()
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private val purchaseWithBnbDialogTitle: TextValue by lazy {
        StringKey.PurchaseDialogWithBnbTitle.textValue(args.type.name)
    }

    private var txHash: String? = null

    init {
        subscribeToMysteryBox()
        subscribeToBnbRate()
    }

    private fun initialUiState(): UiState {
        return UiState(
            type = args.type,
            isPurchasableWithBnb = featureToggle.isEnabled(Feature.PurchaseNftWithBnb),
        )
    }

    private fun subscribeToBnbRate() {
        walletRepository.snpsAccountState.map {
            val cost = nftRepository.mysteryBoxState.value.dataOrCache?.find { it.type == args.type }?.fiatCost
            cost?.toCoin(it.dataOrCache?.usdBnbExchangeRate ?: 0.0)
        }.onEach { coin ->
            _uiState.update { it.copy(costInCoin = coin) }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToMysteryBox() {
        nftRepository.mysteryBoxState.onEach { state ->
            _uiState.update {
                it.copy(
                    mysteryBoxInfoTile = state.toMysteryBoxInfoTileState(
                        type = args.type,
                        onReloadClicked = ::onReloadClicked,
                    ),
                    items = state.dataOrCache?.find { it.type == args.type }?.let { mysteryBox ->
                        getItems(mysteryBox)
                    },
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun getItems(model: MysteryBoxModel) = buildList {
        var nfts = model.marketingProbabilities
        if (model.type == MysteryBoxType.FirstTier) {
            this += CellTileState.Data(
                middlePart = MiddlePart.Data(
                    value = NftType.Follower.displayName.textValue(),
                    valueColor = Color.White,
                    action = StringKey.MysteryBoxTitleGuaranteed.textValue(NftType.Follower.displayName),
                    actionColor = Color.White,
                ),
                leftPart = LeftPart.Icon(
                    icon = NftType.Follower.getSunglassesImage(),
                    tint = Color.Unspecified,
                    background = Color.Transparent,
                )
            )
            nfts = nfts.filter { it.nftType != NftType.Sponsor }
        }
        if (model.type == MysteryBoxType.SecondTier) {
            this += CellTileState.Data(
                middlePart = MiddlePart.Data(
                    value = NftType.Liker.displayName.textValue(),
                    valueColor = Color.White,
                    action = StringKey.MysteryBoxFieldDropRate.textValue(),
                    actionColor = Color.White,
                ),
                leftPart = LeftPart.Icon(
                    icon = NftType.Liker.getSunglassesImage(),
                    tint = Color.Unspecified,
                    background = Color.Transparent,
                    size = 64.dp,
                )
            )
            this += CellTileState.Data(
                middlePart = MiddlePart.Data(
                    value = NftType.Sponsor.displayName.textValue(),
                    valueColor = Color.White,
                    action = StringKey.RankSelectionFieldGuaranteed.textValue(),
                    actionColor = Color.White,
                ),
                leftPart = LeftPart.Icon(
                    icon = NftType.Sponsor.getSunglassesImage(),
                    tint = Color.Unspecified,
                    background = Color.Transparent,
                    size = 64.dp,
                )
            )
            nfts = nfts.filter { it.nftType != NftType.Follower }
        }
        nfts.filter { it.probability != null }
            .forEach {
                this += CellTileState.Data(
                    middlePart = MiddlePart.Data(
                        value = it.nftType.displayName.textValue(),
                        valueColor = Color.White,
                        action = StringKey.MysteryBoxFieldDropChance.textValue("${it.probability!!.toInt()}%"),
                        actionColor = Color.White,
                    ),
                    leftPart = LeftPart.Icon(
                        icon = it.nftType.getSunglassesImage(),
                        tint = Color.Unspecified,
                        background = Color.Transparent,
                        size = 64.dp,
                    )
                )
            }
    }

    fun onBuyWithBNBClicked() {
        viewModelScope.launch {
            showTransferTokensBottomDialog(
                scope = viewModelScope,
                state = TransferTokensState.Shimmer(title = purchaseWithBnbDialogTitle),
            )
            getPurchaseNftWithBnbSummary()
        }
    }

    fun onBottomDialogHidden() {
        _uiState.update { it.copy(bottomDialog = null) }
    }

    // todo copy handler as delegate
    fun onAddressCopyClicked(address: CryptoAddress) {
        viewModelScope.launch {
            _command publish Command.CopyText(address)
            notificationsSource.sendMessage(StringKey.WalletMessageAddressCopied.textValue())
        }
    }

    fun onVideoFinished() {
        viewModelScope.launch {
            _uiState.update { it.copy(uri = null) }
            _command publish Command.BackToMyCollectionScreen(
                data = TransferTokensSuccessData(txHash = txHash.orEmpty(), type = TransferTokensSuccessData.Type.Purchase)
            )
        }
    }

    private suspend fun getPurchaseNftWithBnbSummary() {
        val cost = _uiState.value.costInCoin?.value ?: return
        action.execute {
            interactor.getMysteryBoxMintSummary(cost = cost)
        }.doOnSuccess { summary ->
            updateTransferTokensState(
                state = TransferTokensState.Data(
                    title = purchaseWithBnbDialogTitle,
                    from = summary.from,
                    to = summary.to,
                    summary = CoinBNB(summary.summary.toDouble()),
                    gas = CoinBNB(summary.gas.toDouble()),
                    total = CoinBNB(summary.total.toDouble()),
                    onConfirmClick = { onPurchaseWithBnbConfirmed(summary) },
                    onCancelClick = { hideTransferTokensBottomDialog(viewModelScope) },
                )
            )
        }.doOnError { error, _ ->
            when (error.cause) {
                is NoEnoughBnbToMint -> {
                    onTransferTokensDialogHidden()
                    val walletModel = walletRepository.bnb.value ?: return@doOnError
                    _uiState.update {
                        val qr = barcodeManager.getQrCodeBitmap(walletModel.receiveAddress)
                        it.copy(
                            bottomDialog = BottomDialog.TopUp(
                                title = walletModel.coinType.symbol,
                                address = walletModel.receiveAddress,
                                qr = qr,
                            )
                        )
                    }
                }
                is BalanceInSync -> {
                    onTransferTokensDialogHidden()
                    hideTransferTokensBottomDialog(viewModelScope)
                    notificationsSource.sendError(StringKey.ErrorBalanceInSync.textValue())
                }
                else -> updateTransferTokensState(
                    state = TransferTokensState.Error(
                        title = purchaseWithBnbDialogTitle,
                        onClick = ::onBuyWithBNBClicked,
                    )
                )
            }
        }
    }

    private fun onPurchaseWithBnbConfirmed(summary: NftMintSummary) {
        viewModelScope.launch {
            hideTransferTokensBottomDialog(viewModelScope)
            _uiState.update { it.copy(isLoading = true) }
            action.execute {
                interactor.mysteryBoxMintOnBlockchain(mysteryBoxType = args.type, summary = summary)
            }.doOnSuccess { data ->
                txHash = data.txHash
            }.doOnError { error, _ ->
                if (error.code == 400) notificationsSource.sendError(error)
            }.doOnComplete {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun onReloadClicked() {
        viewModelScope.launch {
            action.execute {
                nftRepository.updateMysteryBoxes()
            }
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val costInCoin: CoinValue? = null,
        val type: MysteryBoxType,
        val isPurchasableWithBnb: Boolean,
        val mysteryBoxInfoTile: MysteryBoxInfoTileState = MysteryBoxInfoTileState.Shimmer,
        val items: List<CellTileState>? = null,
        val bottomDialog: BottomDialog? = null,
        val uri: String? = null,
    )

    sealed class BottomDialog {

        data class TopUp(
            val title: String,
            val address: CryptoAddress,
            val qr: Bitmap?,
        ) : BottomDialog()
    }

    sealed class Command {
        data class BackToMyCollectionScreen(val data: TransferTokensSuccessData? = null) : Command()
        data class CopyText(val text: String) : Command()
    }
}