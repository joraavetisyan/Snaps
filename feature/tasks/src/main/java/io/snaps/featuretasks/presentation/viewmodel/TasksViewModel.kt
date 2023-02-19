package io.snaps.featuretasks.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.featuretasks.data.TasksRepository
import io.snaps.featuretasks.data.model.TaskType
import io.snaps.featuretasks.domain.TaskModel
import io.snaps.featuretasks.presentation.toTaskTileState
import io.snaps.featuretasks.presentation.ui.TaskTileState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val action: Action,
    private val tasksRepository: TasksRepository,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        loadHistoryTasks()
        loadCurrentTasks()
    }

    private fun loadHistoryTasks() = viewModelScope.launch {
        action.execute {
            tasksRepository.historyTasks()
        }.toTaskTileState(
            onItemClicked = ::onItemClicked,
            onReloadClicked = ::onHistoryReloadClicked,
        ).also { state ->
            _uiState.update {
                it.copy(history = state)
            }
        }
    }

    private fun loadCurrentTasks() = viewModelScope.launch {
        action.execute {
            tasksRepository.currentTasks()
        }.toTaskTileState(
            onItemClicked = ::onItemClicked,
            onReloadClicked = ::onCurrentReloadClicked,
        ).also { state ->
            _uiState.update {
                it.copy(current = state)
            }
        }
    }

    private fun onHistoryReloadClicked() {
        loadHistoryTasks()
    }

    private fun onCurrentReloadClicked() {
        loadCurrentTasks()
    }

    private fun onItemClicked(task: TaskModel) = viewModelScope.launch {
        val command = when (task.type) {
            TaskType.Share -> Command.OpenShareTaskScreen(task.id)
            TaskType.FindPoints -> Command.OpenFindPointsTaskScreen(task.id)
            TaskType.LikeAndSubscribe -> Command.OpenLikeAndSubscribeTaskScreen(task.id)
            TaskType.WatchVideo -> Command.OpenWatchVideoTaskScreen(task.id)
        }
        _command publish command
    }

    data class UiState(
        val current: List<TaskTileState> = List(6) { TaskTileState.Shimmer },
        val history: List<TaskTileState> = List(6) { TaskTileState.Shimmer },
    )

    sealed class Command {
        data class OpenFindPointsTaskScreen(val id: Uuid) : Command()
        data class OpenLikeAndSubscribeTaskScreen(val id: Uuid) : Command()
        data class OpenShareTaskScreen(val id: Uuid) : Command()
        data class OpenWatchVideoTaskScreen(val id: Uuid) : Command()
    }
}