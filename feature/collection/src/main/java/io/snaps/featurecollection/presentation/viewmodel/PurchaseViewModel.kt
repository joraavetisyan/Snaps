package io.snaps.featurecollection.presentation.viewmodel

import android.app.Activity
import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basebilling.BillingRouter
import io.snaps.basebilling.PurchaseStateProvider
import io.snaps.basenft.data.NftRepository
import io.snaps.basenft.domain.RankModel
import io.snaps.basenft.ui.getSunglassesImage
import io.snaps.basesources.NotificationsSource
import io.snaps.basesources.featuretoggle.Feature
import io.snaps.basesources.featuretoggle.FeatureToggle
import io.snaps.basewallet.data.WalletRepository
import io.snaps.basewallet.domain.NftMintSummary
import io.snaps.basewallet.ui.LimitedGasDialogHandler
import io.snaps.basewallet.ui.TransferTokensDialogHandler
import io.snaps.basewallet.ui.TransferTokensState
import io.snaps.basewallet.ui.TransferTokensSuccessData
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.CoinBNB
import io.snaps.corecommon.model.CoinValue
import io.snaps.corecommon.model.FiatValue
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.Token
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.corecommon.model.CryptoAddress
import io.snaps.coreui.barcode.BarcodeManager
import io.snaps.featurecollection.domain.BalanceInSync
import io.snaps.featurecollection.domain.MyCollectionInteractor
import io.snaps.featurecollection.domain.NoEnoughBnbToMint
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
class PurchaseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    transferTokensDialogHandler: TransferTokensDialogHandler,
    limitedGasDialogHandler: LimitedGasDialogHandler,
    private val featureToggle: FeatureToggle,
    @Bridged private val nftRepository: NftRepository,
    @Bridged private val walletRepository: WalletRepository,
    private val action: Action,
    private val notificationsSource: NotificationsSource,
    private val purchaseStateProvider: PurchaseStateProvider,
    private val billingRouter: BillingRouter,
    private val interactor: MyCollectionInteractor,
    private val barcodeManager: BarcodeManager,
) : SimpleViewModel(),
    TransferTokensDialogHandler by transferTokensDialogHandler,
    LimitedGasDialogHandler by limitedGasDialogHandler {

    private val args = savedStateHandle.requireArgs<AppRoute.Purchase.Args>()
    private val nft = requireNotNull(nftRepository.ranksState.value.dataOrCache?.first { it.type == args.type })

    private val _uiState = MutableStateFlow(initialUiState(nft))
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private val purchaseWithBnbDialogTitle: TextValue by lazy {
        StringKey.PurchaseDialogWithBnbTitle.textValue(args.type.name)
    }

    init {
        subscribeOnNewPurchases()
        subscribeToBnbRate()
    }

    private fun initialUiState(model: RankModel): UiState {
        val isFreePurchased = nftRepository.nftCollectionState.value.dataOrCache?.any {
            it.type == NftType.Free
        } ?: false
        val prevNftImage = (model.type.intType - 1).let {
            if (it != -1) NftType.byIntType(it).getSunglassesImage()
            else NftType.Free.getSunglassesImage()
        }
        return UiState(
            nftType = model.type,
            nftImage = model.image,
            costInFiat = model.cost,
            dailyUnlock = model.dailyUnlock,
            dailyReward = model.dailyReward,
            isPurchasable = model.isPurchasable,
            isPurchasableWithBnb = model.type != NftType.Free && featureToggle.isEnabled(Feature.PurchaseNftWithBnb),
            prevNftImage = prevNftImage,
            isPurchasableForFree = model.type == NftType.Free && !isFreePurchased,
        )
    }

    private fun subscribeOnNewPurchases() {
        purchaseStateProvider.newPurchasesFlow.onEach {
            mint(purchaseToken = it.first().purchaseToken)
        }.launchIn(viewModelScope)
    }

    private fun subscribeToBnbRate() {
        walletRepository.snpsAccountState.map {
            nft.cost?.toCoin(it.dataOrCache?.usdBnbExchangeRate ?: 0.0)
        }.onEach { coin ->
            _uiState.update { it.copy(costInCoin = coin) }
        }.launchIn(viewModelScope)
    }

    private fun mint(purchaseToken: Token? = null) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            action.execute {
                interactor.mint(
                    nftType = args.type,
                    purchaseToken = purchaseToken,
                )
            }.doOnComplete {
                _uiState.update { it.copy(isLoading = false) }
            }.doOnSuccess {
                if (it.isEmpty()) {
                    notificationsSource.sendMessage(StringKey.PurchaseMessageSuccess.textValue())
                    _command publish Command.BackToMyCollectionScreen()
                } else {
                    _command publish Command.BackToMyCollectionScreen(
                        data = TransferTokensSuccessData(txHash = it, type = TransferTokensSuccessData.Type.Purchase)
                    )
                }
            }.doOnError { error, _ ->
                if (error.cause is BalanceInSync) {
                    notificationsSource.sendMessage(StringKey.ErrorBalanceInSync.textValue())
                }
            }
        }
    }

    fun onBuyWithGooglePlayClicked(activity: Activity) {
        viewModelScope.launch {
            purchaseStateProvider.getInAppProducts().data.orEmpty().firstOrNull {
                it.details.sku == args.type.storeId
            }?.let {
                billingRouter.openBillingScreen(it, activity)
            } ?: run {
                notificationsSource.sendError(StringKey.Error.textValue())
            }
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

    private suspend fun getPurchaseNftWithBnbSummary() {
        val cost = _uiState.value.costInCoin?.value ?: return
        action.execute {
            interactor.getNftMintSummary(nftType = args.type, cost = cost)
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
                    notificationsSource.sendMessage(StringKey.ErrorBalanceInSync.textValue())
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
                interactor.mintOnBlockchain(nftType = args.type, summary = summary)
            }.doOnSuccess { hash ->
                _command publish Command.BackToMyCollectionScreen(
                    data = TransferTokensSuccessData(txHash = hash, type = TransferTokensSuccessData.Type.Purchase)
                )
            }.doOnError { error, _ ->
                if (error.code == 400) notificationsSource.sendError(error)
            }.doOnComplete {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onFreeClicked() = mint()

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

    data class UiState(
        val isLoading: Boolean = false,
        val nftType: NftType,
        val prevNftImage: ImageValue,
        val nftImage: ImageValue,
        val costInFiat: FiatValue?,
        val costInCoin: CoinValue? = null,
        val dailyReward: CoinValue,
        val dailyUnlock: Double,
        val isPurchasable: Boolean,
        val isPurchasableWithBnb: Boolean,
        val isPurchasableForFree: Boolean,
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
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
        data class CopyText(val text: String) : Command()
    }
}