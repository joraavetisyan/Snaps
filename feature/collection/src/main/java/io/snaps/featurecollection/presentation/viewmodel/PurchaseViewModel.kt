package io.snaps.featurecollection.presentation.viewmodel

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basebilling.BillingRouter
import io.snaps.basebilling.PurchaseStateProvider
import io.snaps.basenft.data.NftRepository
import io.snaps.basewallet.data.WalletRepository
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.model.FiatCurrency
import io.snaps.corecommon.model.NftType
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
    private val action: Action,
    private val purchaseStateProvider: PurchaseStateProvider,
    private val billingRouter: BillingRouter,
    private val walletRepository: WalletRepository,
    private val nftRepository: NftRepository,
    savedStateHandle: SavedStateHandle,
) : SimpleViewModel() {

    private val args = savedStateHandle.requireArgs<AppRoute.Purchase.Args>()

    private val _uiState = MutableStateFlow(
        UiState(
            nftType = args.type,
            nftImage = ImageValue.Url(args.image),
            cost = "${args.costInUsd}${FiatCurrency.USD.symbol}",
            dailyUnlock = args.dailyUnlock * 100,
            dailyReward = args.dailyReward,
            isAvailableToPurchase = args.isAvailableToPurchase,
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
            _uiState.update { it.copy(isLoading = true) }
            action.execute {
                nftRepository.mintNft(
                    type = args.type,
                    walletAddress = walletRepository.getActiveWalletsReceiveAddresses().first(),
                    purchaseId = it.first().orderId
                )
            }.doOnComplete {
                _uiState.update { it.copy(isLoading = false) }
            }
        }.launchIn(viewModelScope)
    }

    fun onBuyClicked(activity: Activity) {
        viewModelScope.launch {
            if (args.type == NftType.Free) {
                action.execute {
                    nftRepository.mintNft(
                        type = NftType.Free,
                        walletAddress = walletRepository.getActiveWalletsReceiveAddresses().first(),
                        purchaseId = null,
                    )
                }.doOnComplete {
                    _uiState.update { it.copy(isLoading = false) }
                    _command publish Command.OpenMainScreen
                }
            } else {
                purchaseStateProvider.getInAppProducts().data.orEmpty().firstOrNull {
                    it.details.sku == args.type.storeId
                }?.let {
                    billingRouter.openBillingScreen(it, activity)
                }
            }
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val nftImage: ImageValue,
        val nftType: NftType,
        val cost: String,
        val dailyReward: Int,
        val dailyUnlock: Double,
        val isAvailableToPurchase: Boolean,
    )

    sealed class Command {
        object OpenMainScreen : Command()
    }
}