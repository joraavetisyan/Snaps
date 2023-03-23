package io.snaps.featurecollection.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.featurecollection.data.MyCollectionRepository
import io.snaps.featurecollection.presentation.screen.CollectionItemState
import io.snaps.featurecollection.presentation.toMysteryBoxCollectionItemState
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
    private val myCollectionRepository: MyCollectionRepository,
) : SimpleViewModel(), MainHeaderHandler by mainHeaderHandlerDelegate {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        viewModelScope.launch {
            action.execute {
                myCollectionRepository.loadRanks()
            }.doOnSuccess {
                val availableToPurchaseNfts = myCollectionRepository.ranksState.value.dataOrCache
                    ?.count { it.isAvailableToPurchase } ?: 0
                subscribeOnNft(availableToPurchaseNfts)
                subscribeOnMysteryBox(availableToPurchaseNfts)
                loadNft()
                loadMysteryBox()
            }
        }
    }

    private fun subscribeOnNft(maxCount: Int) {
        myCollectionRepository.nftCollectionState.map {
            it.toNftCollectionItemState(
                maxCount = maxCount,
                onAddItemClicked = ::onAddItemClicked,
                onReloadClicked = ::onNftReloadClicked,
            )
        }.onEach { state ->
            _uiState.update { it.copy(nft = state) }
        }.launchIn(viewModelScope)
    }

    private fun subscribeOnMysteryBox(maxCount: Int) {
        myCollectionRepository.mysteryBoxCollectionState.map {
            it.toMysteryBoxCollectionItemState(
                maxCount = maxCount,
                onAddItemClicked = ::onAddItemClicked,
                onReloadClicked = ::onMysteryBoxReloadClicked,
            )
        }.onEach { state ->
            _uiState.update { it.copy(mysteryBox = state) }
        }.launchIn(viewModelScope)
    }

    private fun loadNft() = viewModelScope.launch {
        action.execute {
            myCollectionRepository.loadNftCollection()
        }
    }

    private fun loadMysteryBox() = viewModelScope.launch {
        action.execute {
            myCollectionRepository.loadMysteryBoxCollection()
        }
    }

    private fun onNftReloadClicked() {
        loadNft()
    }

    private fun onMysteryBoxReloadClicked() {
        loadMysteryBox()
    }

    private fun onAddItemClicked() = viewModelScope.launch {
        _command publish Command.OpenRankSelectionScreen
    }

    data class UiState(
        val nft: List<CollectionItemState> = List(6) { CollectionItemState.Shimmer },
        val mysteryBox: List<CollectionItemState> = List(6) { CollectionItemState.Shimmer },
    )

    sealed class Command {
        object OpenRankSelectionScreen : Command()
    }
}