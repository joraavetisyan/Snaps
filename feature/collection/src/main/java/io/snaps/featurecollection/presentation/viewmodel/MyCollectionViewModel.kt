package io.snaps.featurecollection.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basenft.data.NftRepository
import io.snaps.basenft.domain.NftModel
import io.snaps.basenft.ui.CollectionItemState
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.basesession.data.OnboardingHandler
import io.snaps.basesources.NotificationsSource
import io.snaps.featurecollection.domain.NoEnoughSnpToRepair
import io.snaps.basewallet.ui.TransferTokensDialogHandler
import io.snaps.basewallet.ui.TransferTokensSuccessData
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.OnboardingType
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LEVEL_URL = "https://snaps-docs.gitbook.io/baza-znanii-snaps/features/level-up"

@HiltViewModel
class MyCollectionViewModel @Inject constructor(
    @Bridged mainHeaderHandler: MainHeaderHandler,
    onboardingHandler: OnboardingHandler,
    transferTokensDialogHandler: TransferTokensDialogHandler,
    private val action: Action,
    private val notificationsSource: NotificationsSource,
    @Bridged private val nftRepository: NftRepository,
    private val interactor: MyCollectionInteractor,
) : SimpleViewModel(),
    MainHeaderHandler by mainHeaderHandler,
    OnboardingHandler by onboardingHandler,
    TransferTokensDialogHandler by transferTokensDialogHandler {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        subscribeOnNft()

        refreshNfts()

        checkOnboarding(OnboardingType.Nft)
    }

    private fun subscribeOnNft() {
        nftRepository.nftCollectionState.map {
            it.toNftCollectionItemState(
                onAddItemClicked = ::onAddItemClicked,
                onReloadClicked = ::refreshNfts,
                onRepairClicked = ::onRepairClicked,
                onItemClicked = ::onItemClicked,
                onProcessingClicked = ::onProcessingClicked,
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
            }
        }.doOnError { error, _ ->
            if (error.cause is NoEnoughSnpToRepair) {
                notificationsSource.sendError("No enough SNP to repair".textValue()) // todo localize
            }
        }.doOnComplete {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun onProcessingClicked(nftModel: NftModel) {
        // do nothing
    }

    private fun onItemClicked(nftModel: NftModel) {
        if (nftModel.isProcessed) return
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