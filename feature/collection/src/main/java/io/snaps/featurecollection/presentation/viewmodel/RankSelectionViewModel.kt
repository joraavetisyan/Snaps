package io.snaps.featurecollection.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basenft.data.NftRepository
import io.snaps.basenft.domain.NftModel
import io.snaps.basenft.domain.RankModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.basesession.AppRouteProvider
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
    mainHeaderHandlerDelegate: MainHeaderHandler,
    private val action: Action,
    private val nftRepository: NftRepository,
    private val appRouteProvider: AppRouteProvider,
) : SimpleViewModel(), MainHeaderHandler by mainHeaderHandlerDelegate {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private var ranksLoadJob: Job? = null

    init {
        _uiState.update {
            it.copy(
                isMainHeaderItemsEnabled = appRouteProvider.appRouteState.value == AppRoute.MainBottomBar.path(),
            )
        }

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

    data class UiState(
        val isMainHeaderItemsEnabled: Boolean = true,
        val ranks: List<RankTileState> = List(6) { RankTileState.Shimmer },
    )

    sealed class Command {
        object OpenMainScreen : Command()
        data class OpenPurchase(val args: AppRoute.Purchase.Args) : Command()
    }
}