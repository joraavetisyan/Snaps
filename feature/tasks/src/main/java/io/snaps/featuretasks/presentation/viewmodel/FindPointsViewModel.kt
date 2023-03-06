package io.snaps.featuretasks.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.featuretasks.data.TasksRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class FindPointsViewModel @Inject constructor(
    private val action: Action,
    private val tasksRepository: TasksRepository,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onPointIdChanged(pointId: String) {
        _uiState.update {
            it.copy(pointId = pointId)
        }
    }

    fun onConnectButtonClicked() { /*todo*/ }

    fun onVerifyButtonClicked() { /*todo*/ }

    fun onPointsNotFoundButtonClicked() { /*todo*/ }

    data class UiState(
        val energy: Int = 15,
        val pointId: String = "",
    )

    sealed class Command
}