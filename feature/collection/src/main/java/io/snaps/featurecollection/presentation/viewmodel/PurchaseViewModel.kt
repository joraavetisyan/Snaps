package io.snaps.featurecollection.presentation.viewmodel

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basebilling.BillingRouter
import io.snaps.basebilling.PurchaseStateProvider
import io.snaps.basesources.NotificationsSource
import io.snaps.basesources.featuretoggle.Feature
import io.snaps.basesources.featuretoggle.FeatureToggle
import io.snaps.basewallet.domain.NftMintSummary
import io.snaps.basewallet.domain.NoEnoughBnbToMint
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.toStringValue
import io.snaps.corecommon.model.FiatCurrency
import io.snaps.corecommon.model.FullUrl
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
import io.snaps.featurecollection.presentation.screen.PurchaseWithBnbState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PurchaseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    featureToggle: FeatureToggle,
    private val action: Action,
    private val purchaseStateProvider: PurchaseStateProvider,
    private val billingRouter: BillingRouter,
    private val interactor: MyCollectionInteractor,
    private val notificationsSource: NotificationsSource,
) : SimpleViewModel() {

    private val args = savedStateHandle.requireArgs<AppRoute.Purchase.Args>()

    private val _uiState = MutableStateFlow(
        UiState(
            nftType = args.type,
            nftImage = ImageValue.Url(args.image),
            cost = "${args.costInUsd}${FiatCurrency.USD.symbol}",
            dailyUnlock = args.dailyUnlock,
            dailyReward = args.dailyReward,
            isPurchasable = args.isPurchasable,
            isPurchasableWithBnb = featureToggle.isEnabled(Feature.PurchaseNftWithBnb),
            sunglassesImage = getSunglassesImage(),
            purchaseWithBnbState = PurchaseWithBnbState.Shimmer(nftType = args.type),
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

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
            notificationsSource.sendMessage("Purchase successful".textValue()) // todo localize
            _command publish Command.BackToMyCollectionScreen
        }.doOnError { error, _ ->
            notificationsSource.sendError(error)
        }.doOnComplete {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onBuyWithGooglePlayClicked(activity: Activity) {
        viewModelScope.launch {
            _command publish Command.BackToMyCollectionScreen
            purchaseStateProvider.getInAppProducts().data.orEmpty().firstOrNull {
                it.details.sku == args.type.storeId
            }?.let {
                billingRouter.openBillingScreen(it, activity)
            }
        }
    }

    fun onBuyWithBNBClicked() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    bottomDialog = BottomDialog.PurchaseWithBnb,
                    purchaseWithBnbState = PurchaseWithBnbState.Shimmer(nftType = args.type),
                )
            }
            _command publish Command.ShowBottomDialog
            getPurchaseNftWithBnbSummary()
        }
    }

    private suspend fun getPurchaseNftWithBnbSummary() {
        action.execute {
            interactor.getNftMintSummary(args.type)
        }.doOnSuccess { summary ->
            _uiState.update {
                it.copy(
                    purchaseWithBnbState = PurchaseWithBnbState.Data(
                        nftType = args.type,
                        from = summary.from,
                        to = summary.to,
                        // todo localize
                        summary = summary.summary.toDouble().toStringValue() + " BNB",
                        gas = summary.gas.toDouble().toStringValue() + " BNB",
                        total = summary.total.toDouble().toStringValue() + " BNB",
                        onConfirmClick = { onConfirmed(summary) },
                        onCancelClick = {
                            viewModelScope.launch {
                                _command publish Command.HideBottomDialog
                            }
                        },
                    )
                )
            }
        }.doOnError { error, _ ->
            _uiState.update {
                it.copy(
                    purchaseWithBnbState = PurchaseWithBnbState.Error(
                        nftType = args.type,
                        message = MessageBannerState(
                            icon = AppTheme.specificIcons.reload.toImageValue(),
                            description = "Not enough BNB to mint".textValue(), // todo localize
                            button = StringKey.ActionClose.textValue(),
                            onClick = {
                                viewModelScope.launch {
                                    _command publish Command.HideBottomDialog
                                }
                            }
                        ).takeIf { error.cause is NoEnoughBnbToMint },
                        onClick = ::onBuyWithBNBClicked,
                    )
                )
            }
        }
    }

    private fun onConfirmed(summary: NftMintSummary) {
        viewModelScope.launch {
            _command publish Command.HideBottomDialog
            _uiState.update { it.copy(isLoading = true) }
            action.execute {
                interactor.mintOnBlockchain(nftType = args.type, summary = summary)
            }.doOnSuccess { hash ->
                _uiState.update {
                    it.copy(bottomDialog = BottomDialog.PurchaseWithBnbSuccess("https://testnet.bscscan.com/tx/${hash}"))
                }
                _command publish Command.ShowBottomDialog
            }.doOnComplete {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onFreeClicked() = viewModelScope.launch { mint() }

    private fun getSunglassesImage() = when (args.type.intType) {
        1 -> R.drawable.img_sunglasses0
        2 -> R.drawable.img_sunglasses1
        3 -> R.drawable.img_sunglasses2
        4 -> R.drawable.img_sunglasses3
        5 -> R.drawable.img_sunglasses4
        6 -> R.drawable.img_sunglasses5
        7 -> R.drawable.img_sunglasses6
        8 -> R.drawable.img_sunglasses7
        9 -> R.drawable.img_sunglasses8
        10 -> R.drawable.img_sunglasses9
        11 -> R.drawable.img_sunglasses10
        else -> null
    }?.let(ImageValue::ResImage)

    data class UiState(
        val isLoading: Boolean = false,
        val nftImage: ImageValue,
        val nftType: NftType,
        val cost: String,
        val dailyReward: Int,
        val dailyUnlock: Double,
        val isPurchasable: Boolean,
        val isPurchasableWithBnb: Boolean,
        val sunglassesImage: ImageValue? = null,
        val bottomDialog: BottomDialog = BottomDialog.PurchaseWithBnb,
        val purchaseWithBnbState: PurchaseWithBnbState,
    )

    sealed class BottomDialog {
        object PurchaseWithBnb : BottomDialog()
        data class PurchaseWithBnbSuccess(val link: FullUrl) : BottomDialog()
    }

    sealed class Command {
        object BackToMyCollectionScreen : Command()
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
    }
}