package io.snaps.featurecollection.presentation.viewmodel

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basebilling.BillingRouter
import io.snaps.basebilling.PurchaseStateProvider
import io.snaps.basenft.data.NftRepository
import io.snaps.basesources.NotificationsSource
import io.snaps.basesources.featuretoggle.Feature
import io.snaps.basesources.featuretoggle.FeatureToggle
import io.snaps.basewallet.domain.NftMintSummary
import io.snaps.basewallet.domain.NoEnoughBnbToMint
import io.snaps.basewallet.ui.LimitedGasDialogHandler
import io.snaps.basewallet.ui.TransferTokensDialogHandler
import io.snaps.basewallet.ui.TransferTokensState
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.toStringValue
import io.snaps.corecommon.model.FiatCurrency
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.Token
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featurecollection.domain.MyCollectionInteractor
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val minGasValue = 0.0012

@HiltViewModel
class PurchaseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    featureToggle: FeatureToggle,
    transferTokensDialogHandlerImplDelegate: TransferTokensDialogHandler,
    limitedGasDialogHandlerImplDelegate: LimitedGasDialogHandler,
    nftRepository: NftRepository,
    private val action: Action,
    private val notificationsSource: NotificationsSource,
    private val purchaseStateProvider: PurchaseStateProvider,
    private val billingRouter: BillingRouter,
    private val interactor: MyCollectionInteractor,
) : SimpleViewModel(),
    TransferTokensDialogHandler by transferTokensDialogHandlerImplDelegate,
    LimitedGasDialogHandler by limitedGasDialogHandlerImplDelegate {

    private val args = savedStateHandle.requireArgs<AppRoute.Purchase.Args>()

    private val _uiState = MutableStateFlow(
        UiState(
            nftType = args.type,
            nftImage = ImageValue.Url(args.image),
            // todo currency name
            cost = "${args.costInUsd}${FiatCurrency.USD.symbol}",
            dailyUnlock = args.dailyUnlock,
            dailyReward = args.dailyReward,
            isPurchasable = args.isPurchasable,
            isPurchasableWithBnb = args.type != NftType.Free && featureToggle.isEnabled(Feature.PurchaseNftWithBnb),
            prevNftImage = (args.type.intType - 1).let {
                if (it != -1) {
                    NftType.fromIntType(it).getSunglassesImage()
                } else {
                    NftType.Free.getSunglassesImage()
                }
            },
            isFreeButtonVisible = args.type == NftType.Free
                && (nftRepository.nftCollectionState.value.dataOrCache?.none { it.type == NftType.Free } ?: true),
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private val purchaseWithBnbDialogTitle: TextValue by lazy {
        StringKey.PurchaseDialogWithBnbTitle.textValue(args.type.name)
    }

    init {
        subscribeOnNewPurchases()
    }

    private fun subscribeOnNewPurchases() = viewModelScope.launch {
        purchaseStateProvider.newPurchasesFlow.onEach {
            mint(purchaseToken = it.first().purchaseToken)
        }.launchIn(viewModelScope)
    }

    private suspend fun mint(purchaseToken: Token? = null) {
        _uiState.update { it.copy(isLoading = true) }
        action.execute {
            interactor.mint(
                nftType = args.type,
                purchaseToken = purchaseToken,
            )
        }.doOnSuccess {
            notificationsSource.sendMessage(StringKey.PurchaseMessageSuccess.textValue())
            _command publish Command.BackToMyCollectionScreen
        }.doOnError { error, _ ->
            notificationsSource.sendError(error)
        }.doOnComplete {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onBuyWithGooglePlayClicked(activity: Activity) {
        viewModelScope.launch {
            purchaseStateProvider.getInAppProducts().data.orEmpty().firstOrNull {
                it.details.sku == args.type.storeId
            }?.let {
                billingRouter.openBillingScreen(it, activity)
            }
        }
    }

    fun onBuyWithBNBClicked() {
        checkGas(scope = viewModelScope, minValue = minGasValue) {
            showTransferTokensBottomDialog(
                scope = viewModelScope,
                state = TransferTokensState.Shimmer(title = purchaseWithBnbDialogTitle)
            )
            getPurchaseNftWithBnbSummary()
        }
    }

    private suspend fun getPurchaseNftWithBnbSummary() {
        action.execute {
            interactor.getNftMintSummary(args.type)
        }.doOnSuccess { summary ->
            updateTransferTokensState(
                state = TransferTokensState.Data(
                    title = purchaseWithBnbDialogTitle,
                    from = summary.from,
                    to = summary.to,
                    // todo currency name
                    summary = summary.summary.toStringValue() + " BNB",
                    gas = summary.gas.toStringValue() + " BNB",
                    total = summary.total.toStringValue() + " BNB",
                    onConfirmClick = { onConfirmed(summary) },
                    onCancelClick = { hideTransferTokensBottomDialog(viewModelScope) },
                )
            )
        }.doOnError { error, _ ->
            updateTransferTokensState(
                state = TransferTokensState.Error(
                    title = purchaseWithBnbDialogTitle,
                    message = MessageBannerState(
                        icon = AppTheme.specificIcons.reload.toImageValue(),
                        description = StringKey.PurchaseErrorNotEnoughBnb.textValue(),
                        button = StringKey.ActionClose.textValue(),
                        onClick = { hideTransferTokensBottomDialog(viewModelScope) },
                    ).takeIf { error.cause is NoEnoughBnbToMint },
                    onClick = ::onBuyWithBNBClicked,
                )
            )
        }
    }

    private fun onConfirmed(summary: NftMintSummary) {
        viewModelScope.launch {
            hideTransferTokensBottomDialog(viewModelScope)
            _uiState.update { it.copy(isLoading = true) }
            action.execute {
                interactor.mintOnBlockchain(nftType = args.type, summary = summary)
            }.doOnSuccess { hash ->
                onSuccessfulTransfer(scope = viewModelScope, txHash = hash)
            }.doOnError { error, _ ->
                if (error.code == 400) notificationsSource.sendError(error)
            }.doOnComplete {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onFreeClicked() = viewModelScope.launch { mint() }

    data class UiState(
        val isLoading: Boolean = false,
        val nftImage: ImageValue,
        val nftType: NftType,
        val cost: String,
        val dailyReward: Int,
        val dailyUnlock: Double,
        val isPurchasable: Boolean,
        val isPurchasableWithBnb: Boolean,
        val prevNftImage: ImageValue,
        val isFreeButtonVisible: Boolean,
    )

    sealed class Command {
        object BackToMyCollectionScreen : Command()
    }
}