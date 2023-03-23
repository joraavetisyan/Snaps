package io.snaps.featurecollection.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.basesources.NotificationsSource
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.featurecollection.data.MyCollectionRepository
import io.snaps.featurecollection.domain.RankModel
import io.snaps.featurecollection.presentation.screen.RankTileState
import io.snaps.featurecollection.presentation.toRankTileState
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
    private val myCollectionRepository: MyCollectionRepository,
    private val notificationsSource: NotificationsSource,
    mainHeaderHandlerDelegate: MainHeaderHandler,
) : SimpleViewModel(), MainHeaderHandler by mainHeaderHandlerDelegate {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        subscribeOnRanks()
        loadRanks()
    }

    private fun subscribeOnRanks() {
        myCollectionRepository.ranksState.map {
            it.toRankTileState(
                onItemClicked = ::onItemClicked,
                onReloadClicked = ::onReloadClicked,
            )
        }.onEach { state ->
            _uiState.update { it.copy(ranks = state) }
        }.launchIn(viewModelScope)
    }

    private fun loadRanks() = viewModelScope.launch {
        action.execute {
            myCollectionRepository.loadRanks()
        }
    }

    private fun onReloadClicked() {
        loadRanks()
    }

    private fun onItemClicked(rank: RankModel) = viewModelScope.launch {
        if (rank.isAvailableToPurchase) {
            _command publish Command.OpenPurchase(
                args = AppRoute.Purchase.Args(
                    type = rank.type,
                    costInUsd = requireNotNull(rank.costInUsd),
                    dailyReward = rank.dailyReward,
                    dailyUnlock = rank.dailyUnlock,
                    image = rank.image.value as FullUrl,
                )
            )
        } else {
            notificationsSource.sendMessage(StringKey.RankSelectionMessageNotAvailable.textValue())
        }
    }

    data class UiState(
        val ranks: List<RankTileState> = List(6) { RankTileState.Shimmer },
    )

    sealed class Command {
        object OpenMainScreen : Command()
        data class OpenPurchase(val args: AppRoute.Purchase.Args) : Command()
    }
}