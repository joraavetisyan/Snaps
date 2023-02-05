package io.snaps.featuretasks.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.featuretasks.domain.Task
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val action: Action,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    data class UiState(
        val current: List<Task> = List(20) {
            Task(
                id = "current$it",
                title = "title$it",
                description = "description$it",
                result = if (it == 0) "15/15" else "2/15",
                isCompleted = it == 0,
                type = Task.Type.FindPoints,
            )
        },
        val history: List<Task> = List(20) {
            Task(
                id = "history$it",
                title = "title$it",
                description = "2 January",
                result = "2/15",
                isCompleted = true,
                type = Task.Type.FindPoints,
            )
        },
    )

    sealed class Command
}