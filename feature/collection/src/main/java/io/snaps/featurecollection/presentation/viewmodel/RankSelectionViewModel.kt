package io.snaps.featurecollection.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.corecommon.model.NftType
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.featurecollection.data.MyCollectionRepository
import io.snaps.featurecollection.domain.RankModel
import io.snaps.featurecollection.presentation.screen.RankTileState
import io.snaps.featurecollection.presentation.toRankTileState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankSelectionViewModel @Inject constructor(
    private val action: Action,
    private val myCollectionRepository: MyCollectionRepository,
    mainHeaderHandlerDelegate: MainHeaderHandler,
) : SimpleViewModel(), MainHeaderHandler by mainHeaderHandlerDelegate {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        loadRanks()
    }

    private fun loadRanks() = viewModelScope.launch {
        action.execute {
            myCollectionRepository.getRanks()
        }.toRankTileState(
            onItemClicked = ::onItemClicked,
            onReloadClicked = ::onReloadClicked,
        ).also { state ->
            _uiState.update {
                it.copy(ranks = state)
            }
        }
    }

    private fun onReloadClicked() {
        loadRanks()
    }

    private fun onItemClicked(rank: RankModel) = viewModelScope.launch {
        if (rank.type == NftType.Free) {
            action.execute {
                myCollectionRepository.mintNft(rank.type)
            }.doOnSuccess {
                _command publish Command.OpenMainScreen
            }
        } else {
            _command publish Command.OpenBuyNft
        }
    }

    data class UiState(
        val ranks: List<RankTileState> = List(6) { RankTileState.Shimmer },
    )

    sealed class Command {
        object OpenMainScreen : Command()
        object OpenBuyNft : Command()
    }
}