package io.snaps.featurecollection.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basenft.data.NftRepository
import io.snaps.basenft.ui.CollectionItemState
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
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
    private val action: Action,
    private val nftRepository: NftRepository,
) : SimpleViewModel(), MainHeaderHandler by mainHeaderHandlerDelegate {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        viewModelScope.launch {
            action.execute {
                nftRepository.updateRanks()
            }.doOnSuccess {
                val availableToPurchaseNfts = nftRepository.ranksState.value.dataOrCache
                    ?.count { it.isAvailableToPurchase } ?: 0
                subscribeOnNft(availableToPurchaseNfts)
                loadNft()
            }
        }
    }

    private fun subscribeOnNft(maxCount: Int) {
        nftRepository.nftCollectionState.map {
            it.toNftCollectionItemState(
                maxCount = maxCount,
                onAddItemClicked = ::onAddItemClicked,
                onReloadClicked = ::onNftReloadClicked,
                onRepairClicked = ::onRepairClicked,
            )
        }.onEach { state ->
            _uiState.update { it.copy(nft = state) }
        }.launchIn(viewModelScope)
    }

    private fun loadNft() = viewModelScope.launch {
        action.execute {
            nftRepository.updateNftCollection()
        }
    }

    private fun onNftReloadClicked() {
        loadNft()
    }

    private fun onAddItemClicked() = viewModelScope.launch {
        _command publish Command.OpenRankSelectionScreen
    }

    private fun onRepairClicked(glassesId: Uuid) = viewModelScope.launch {
        _uiState.update {
            it.copy(isLoading = true)
        }
        action.execute {
            nftRepository.repairGlasses(glassesId)
        }.doOnComplete {
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val nft: List<CollectionItemState> = List(6) { CollectionItemState.Shimmer },
    )

    sealed class Command {
        object OpenRankSelectionScreen : Command()
    }
}