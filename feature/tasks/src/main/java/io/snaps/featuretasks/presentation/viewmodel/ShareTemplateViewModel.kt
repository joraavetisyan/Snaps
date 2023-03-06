package io.snaps.featuretasks.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.featuretasks.data.TasksRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class ShareTemplateViewModel @Inject constructor(
    private val action: Action,
    private val tasksRepository: TasksRepository,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onShareIconClicked() { /*todo*/ }

    fun onSaveButtonClicked() { /*todo*/ }

    data class UiState(
        val isLoading: Boolean = false,
    )

    sealed class Command {
        object OpenMainVideoFeed : Command()
        object OpenShareTemplate : Command()
        object OpenCreateVideo : Command()
    }
}