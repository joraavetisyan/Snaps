package io.snaps.featurecollection.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basenft.data.NftRepository
import io.snaps.basenft.domain.NftModel
import io.snaps.basenft.ui.CollectionItemState
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.basesession.data.OnboardingHandler
import io.snaps.basesources.BottomDialogBarVisibilityHandler
import io.snaps.basesources.NotificationsSource
import io.snaps.basewallet.data.WalletRepository
import io.snaps.basewallet.ui.LimitedGasDialogHandler
import io.snaps.featurecollection.domain.NoEnoughSnpToRepair
import io.snaps.basewallet.ui.TransferTokensDialogHandler
import io.snaps.basewallet.ui.TransferTokensSuccessData
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.OnboardingType
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.featurecollection.domain.MyCollectionInteractor
import io.snaps.featurecollection.presentation.toNftCollectionItemState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LEVEL_URL = "https://snaps-docs.gitbook.io/baza-znanii-snaps/features/level-up"

@HiltViewModel
class MyCollectionViewModel @Inject constructor(
    @Bridged mainHeaderHandler: MainHeaderHandler,
    @Bridged onboardingHandler: OnboardingHandler,
    transferTokensDialogHandler: TransferTokensDialogHandler,
    bottomDialogBarVisibilityHandler: BottomDialogBarVisibilityHandler,
    limitedGasDialogHandler: LimitedGasDialogHandler,
    private val action: Action,
    private val notificationsSource: NotificationsSource,
    @Bridged private val nftRepository: NftRepository,
    @Bridged private val walletRepository: WalletRepository,
    private val interactor: MyCollectionInteractor,
) : SimpleViewModel(),
    MainHeaderHandler by mainHeaderHandler,
    OnboardingHandler by onboardingHandler,
    TransferTokensDialogHandler by transferTokensDialogHandler,
    BottomDialogBarVisibilityHandler by bottomDialogBarVisibilityHandler,
    LimitedGasDialogHandler by limitedGasDialogHandler {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        subscribeOnNft()

        refreshNfts()

        viewModelScope.launch {
            checkOnboarding(OnboardingType.Nft)
        }
    }

    private fun subscribeOnNft() {
        nftRepository.nftCollectionState.combine(walletRepository.snpsAccountState) { collection, account ->
            collection.toNftCollectionItemState(
                snpsUsdExchangeRate = account.dataOrCache?.snpsUsdExchangeRate ?: 0.0,
                onAddItemClicked = ::onAddItemClicked,
                onReloadClicked = ::refreshNfts,
                onRepairClicked = ::onRepairClicked,
                onItemClicked = ::onItemClicked,
                onHelpIconClicked = ::onHelpIconClicked,
            )
        }.onEach { state ->
            _uiState.update { it.copy(nft = state) }
        }.launchIn(viewModelScope)
    }

    private fun refreshNfts() = viewModelScope.launch {
        action.execute {
            nftRepository.updateNftCollection()
        }.doOnComplete {
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    private fun onAddItemClicked() = viewModelScope.launch {
        _command publish Command.OpenRankSelectionScreen
    }

    private fun onRepairClicked(nftModel: NftModel) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        action.execute {
            interactor.repair(nftModel)
        }.doOnSuccess {
            if (it.isNotEmpty()) {
                onSuccessfulTransfer(scope = viewModelScope, data = TransferTokensSuccessData(txHash = it))
            } else {
                notificationsSource.sendMessage(StringKey.MessageSuccess.textValue())
            }
        }.doOnError { error, _ ->
            if (error.cause is NoEnoughSnpToRepair) {
                notificationsSource.sendError(StringKey.MyCollectionErrorNoEnoughSnp.textValue())
            }
        }.doOnComplete {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun onItemClicked(nftModel: NftModel) {
        viewModelScope.launch {
            _command publish Command.OpenNftDetailsScreen(
                args = AppRoute.UserNftDetails.Args(nftId = nftModel.id)
            )
        }
    }

    private fun onHelpIconClicked() {
        viewModelScope.launch {
            _command publish Command.OpenWebViewScreen(LEVEL_URL)
        }
    }

    fun onRefreshPulled() {
        _uiState.update { it.copy(isRefreshing = true) }
        refreshNfts()
    }

    fun onTransactionResultReceived(result: TransferTokensSuccessData?) {
        when (result?.type) {
            TransferTokensSuccessData.Type.Purchase -> {
                updateTotalBalance()
                onSuccessfulPurchase(scope = viewModelScope, data = result)
            }
            TransferTokensSuccessData.Type.Sell,
            TransferTokensSuccessData.Type.Send,
            null -> Unit
        }
    }

    private fun updateTotalBalance() {
        viewModelScope.launch { action.execute { walletRepository.updateTotalBalance() } }
    }

    data class UiState(
        val isRefreshing: Boolean = false,
        val isLoading: Boolean = false,
        val nft: List<CollectionItemState> = List(6) { CollectionItemState.Shimmer },
    )

    sealed class Command {
        object OpenRankSelectionScreen : Command()
        data class OpenNftDetailsScreen(val args: AppRoute.UserNftDetails.Args) : Command()
        data class OpenWebViewScreen(val url: FullUrl) : Command()
    }
}