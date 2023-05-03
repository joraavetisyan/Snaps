package io.snaps.featurecollection.presentation.viewmodel

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basebilling.BillingRouter
import io.snaps.basebilling.PurchaseStateProvider
import io.snaps.basenft.data.NftRepository
import io.snaps.basesources.NotificationsSource
import io.snaps.basewallet.data.WalletRepository
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.FiatCurrency
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.Token
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
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
    private val action: Action,
    private val purchaseStateProvider: PurchaseStateProvider,
    private val billingRouter: BillingRouter,
    private val notificationsSource: NotificationsSource,
    private val walletRepository: WalletRepository,
    private val nftRepository: NftRepository,
) : SimpleViewModel() {

    private val args = savedStateHandle.requireArgs<AppRoute.Purchase.Args>()

    private val _uiState = MutableStateFlow(
        UiState(
            nftType = args.type,
            nftImage = ImageValue.Url(args.image),
            cost = "${args.costInUsd}${FiatCurrency.USD.symbol}",
            dailyUnlock = args.dailyUnlock,
            dailyReward = args.dailyReward,
            isAvailableToPurchase = args.isAvailableToPurchase,
            sunglassesImage = getSunglassesImage(),
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
            minNft(purchaseToken = it.first().purchaseToken)
        }.launchIn(viewModelScope)
    }

    private suspend fun minNft(purchaseToken: Token?) {
        _uiState.update { it.copy(isLoading = true) }
        action.execute {
            nftRepository.mintNft(
                type = args.type,
                walletAddress = walletRepository.getActiveWalletsReceiveAddresses().first(),
                purchaseId = purchaseToken,
            )
        }.doOnSuccess {
            notificationsSource.sendMessage("Purchase successful".textValue()) // todo localize
            _command publish Command.ClosePurchaseScreen
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
        // TODO
    }

    fun onFreeClicked() = viewModelScope.launch {
        minNft(purchaseToken = null)
    }

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
        val isAvailableToPurchase: Boolean,
        val sunglassesImage: ImageValue? = null,
    )

    sealed class Command {
        object ClosePurchaseScreen : Command()
    }
}