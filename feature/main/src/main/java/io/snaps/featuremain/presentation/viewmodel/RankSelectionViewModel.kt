package io.snaps.featuremain.presentation.viewmodel

import io.snaps.corecommon.container.ImageValue
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.featuremain.domain.Rank
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class RankSelectionViewModel @Inject constructor(
    private val action: Action,
    mainHeaderHandlerDelegate: MainHeaderHandler,
) : SimpleViewModel(), MainHeaderHandler by mainHeaderHandlerDelegate {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    data class UiState(
        val ranks: List<Rank> = List(20) {
            Rank(
                type = "Free",
                price = "Free",
                image = ImageValue.Url("https://picsum.photos/100"),
                dailyReward = "0.51\$",
                dailyUnlock = "6%",
                dailyConsumption = "60%",
                dosagePerDayMonth = "30/900 tokens",
                spendingOnGas = "0.11\$",
            )
        },
    )

    sealed class Command
}