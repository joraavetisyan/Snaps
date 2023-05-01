package io.snaps.featurecollection.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basenft.data.NftRepository
import io.snaps.basenft.domain.NftModel
import io.snaps.basenft.domain.RankModel
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.FullUrl
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.featurecollection.presentation.screen.RankTileState
import io.snaps.featurecollection.presentation.toRankTileState
import kotlinx.coroutines.Job
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
class RankSelectionViewModel @Inject constructor(
    private val action: Action,
    private val nftRepository: NftRepository,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private var ranksLoadJob: Job? = null

    init {
        nftRepository.nftCollectionState.onEach {
            if (it is Effect && it.isSuccess) {
                subscribeOnRanks(it.requireData)
                loadRanks()
            }
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            action.execute { nftRepository.updateNftCollection() }
        }
    }

    private fun subscribeOnRanks(purchasedRanks: List<NftModel>) {
        ranksLoadJob?.cancel()
        ranksLoadJob = nftRepository.ranksState.map {
            it.toRankTileState(
                purchasedRanks = purchasedRanks,
                onItemClicked = ::onItemClicked,
                onReloadClicked = ::onReloadClicked,
            )
        }.onEach { state ->
            _uiState.update { it.copy(ranks = state) }
        }.launchIn(viewModelScope)
    }

    private fun loadRanks() = viewModelScope.launch {
        action.execute {
            nftRepository.updateRanks()
        }
    }

    private fun onReloadClicked() {
        loadRanks()
    }

    private fun onItemClicked(rank: RankModel) = viewModelScope.launch {
        _command publish Command.OpenPurchase(
            args = AppRoute.Purchase.Args(
                type = rank.type,
                costInUsd = rank.costInUsd,
                dailyReward = rank.dailyReward,
                dailyUnlock = rank.dailyUnlock,
                image = rank.image.value as FullUrl,
                isAvailableToPurchase = rank.isAvailableToPurchase,
            )
        )
    }

    fun onRankFootnoteClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(bottomDialog = BottomDialog.RankFootnote) }
            _command publish Command.ShowBottomDialog
        }
    }

    fun onRaiseNftRankClick() {
        viewModelScope.launch {
            _command publish Command.HideBottomDialog
        }
    }

    data class UiState(
        val ranks: List<RankTileState> = List(6) { RankTileState.Shimmer },
        val bottomDialog: BottomDialog = BottomDialog.RankFootnote,
    )

    sealed class BottomDialog {
        object RankFootnote : BottomDialog()
    }

    sealed class Command {
        data class OpenPurchase(val args: AppRoute.Purchase.Args) : Command()
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
    }
}