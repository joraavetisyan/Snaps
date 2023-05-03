package io.snaps.featurecollection.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basenft.data.NftRepository
import io.snaps.corecommon.model.NftModel
import io.snaps.basenft.ui.CollectionItemState
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.basesession.data.OnboardingHandler
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.OnboardingType
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

@HiltViewModel
class MyCollectionViewModel @Inject constructor(
    mainHeaderHandlerDelegate: MainHeaderHandler,
    onboardingHandlerDelegate: OnboardingHandler,
    private val action: Action,
    private val nftRepository: NftRepository,
    private val interactor: MyCollectionInteractor,
) : SimpleViewModel(),
    MainHeaderHandler by mainHeaderHandlerDelegate,
    OnboardingHandler by onboardingHandlerDelegate {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        subscribeOnNft()

        updateNft()

        checkOnboarding(OnboardingType.Nft)
    }

    private fun subscribeOnNft() {
        nftRepository.nftCollectionState.map {
            it.toNftCollectionItemState(
                onAddItemClicked = ::onAddItemClicked,
                onReloadClicked = ::onNftReloadClicked,
                onRepairClicked = ::onRepairClicked,
                onItemClicked = ::onItemClicked,
            )
        }.onEach { state ->
            _uiState.update { it.copy(nft = state) }
        }.launchIn(viewModelScope)
    }

    private fun updateNft() = viewModelScope.launch {
        action.execute {
            nftRepository.updateNftCollection()
        }
    }

    private fun onNftReloadClicked() {
        updateNft()
    }

    private fun onAddItemClicked() = viewModelScope.launch {
        _command publish Command.OpenRankSelectionScreen
    }

    private fun onRepairClicked(nftModel: NftModel) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        action.execute {
            interactor.repairGlasses(nftModel)
        }.doOnComplete {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun onItemClicked(nftModel: NftModel) {
        viewModelScope.launch {
            _command publish Command.OpenNftDetailsScreen(
                args = AppRoute.NftDetails.Args(
                    type = nftModel.type,
                    dailyReward = nftModel.dailyReward,
                    image = nftModel.image.value as FullUrl,
                )
            )
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val nft: List<CollectionItemState> = List(6) { CollectionItemState.Shimmer },
    )

    sealed class Command {
        object OpenRankSelectionScreen : Command()
        data class OpenNftDetailsScreen(val args: AppRoute.NftDetails.Args) : Command()
    }
}