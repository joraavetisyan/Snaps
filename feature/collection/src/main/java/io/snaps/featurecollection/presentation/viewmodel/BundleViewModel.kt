package io.snaps.featurecollection.presentation.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basenft.data.NftRepository
import io.snaps.basenft.domain.BundleModel
import io.snaps.basenft.domain.RankModel
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
import io.snaps.corecommon.model.BundleType
import io.snaps.corecommon.model.CoinBNB
import io.snaps.corecommon.model.CoinValue
import io.snaps.corecommon.model.CryptoAddress
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.barcode.BarcodeManager
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.featurecollection.domain.BalanceInSync
import io.snaps.featurecollection.domain.MyCollectionInteractor
import io.snaps.featurecollection.domain.NoEnoughBnbToMint
import io.snaps.featurecollection.presentation.screen.BundleTileState
import io.snaps.featurecollection.presentation.screen.RankTileState
import io.snaps.featurecollection.presentation.toBundleTileState
import io.snaps.featurecollection.presentation.toRankTileState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BundleViewModel @Inject constructor(
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
) : SimpleViewModel(),
    TransferTokensDialogHandler by transferTokensDialogHandler,
    LimitedGasDialogHandler by limitedGasDialogHandler {

    private val args = savedStateHandle.requireArgs<AppRoute.Bundle.Args>()

    private val _uiState = MutableStateFlow(
        initialUiState()
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private val purchaseWithBnbDialogTitle: TextValue by lazy {
        StringKey.PurchaseDialogWithBnbTitle.textValue(args.type.name)
    }

    init {
        subscribeToBnbRate()
        nftRepository.bundleState.value.dataOrCache?.let { bundles ->
            val bundleModel = bundles.first { it.type == args.type }
            subscribeToRanks(bundleModel)
            _uiState.update {
                it.copy(bundle = bundleModel.toBundleTileState(onItemClicked = {}))
            }
        }
    }

    private fun initialUiState(): UiState {
        return UiState(
            type = args.type,
            isPurchasableWithBnb = featureToggle.isEnabled(Feature.PurchaseNftWithBnb),
        )
    }

    private fun subscribeToBnbRate() {
        walletRepository.snpsAccountState.map {
            val bundle = nftRepository.bundleState.value.dataOrCache ?.find { it.type == args.type }
            bundle?.fiatCost?.toCoin(it.dataOrCache?.usdBnbExchangeRate ?: 0.0)
        }.onEach { coin ->
            _uiState.update { it.copy(costInCoin = coin) }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToRanks(model: BundleModel) {
        nftRepository.ranksState.combine(walletRepository.snpsAccountState) { ranks, account ->
            if (ranks is Effect<List<RankModel>>) {
                val bundleNft = ranks.requireData
                    .filter { it.type in model.itemsInBundle }
                    .map { it.copy(isPurchasable = true) }
                bundleNft.map {
                    it.toRankTileState(
                        snpsUsdExchangeRate = account.dataOrCache?.snpsUsdExchangeRate ?: 0.0,
                        onItemClicked = {},
                    )
                }
            } else emptyList()
        }.onEach { state ->
            _uiState.update { it.copy(itemsInBundle = state) }
        }.launchIn(viewModelScope)
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

    private suspend fun getPurchaseNftWithBnbSummary() {
        val cost = _uiState.value.costInCoin?.value ?: return
        action.execute {
            interactor.getBundlesMintSummary(type = args.type, cost = cost)
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
                interactor.bundlesMintOnBlockchain(bundleType = args.type, summary = summary)
            }.doOnSuccess { data ->
                _command publish Command.BackToMyCollectionScreen(
                    data = TransferTokensSuccessData(
                        txHash = data.txHash.orEmpty(),
                        type = TransferTokensSuccessData.Type.Purchase,
                    )
                )
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
        val type: BundleType,
        val isPurchasableWithBnb: Boolean,
        val bundle: BundleTileState = BundleTileState.Shimmer,
        val itemsInBundle: List<RankTileState> = List(2) { RankTileState.Shimmer },
        val bottomDialog: BottomDialog? = null,
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