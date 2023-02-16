package io.snaps.featuretasks.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.featuretasks.data.TasksRepository
import io.snaps.featuretasks.domain.TaskModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val action: Action,
    private val tasksRepository: TasksRepository,
) : SimpleViewModel() {

    private val args = savedStateHandle.requireArgs<AppRoute.TaskArgs>()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        loadTask()
    }

    private fun loadTask() = viewModelScope.launch {
        action.execute {
            tasksRepository.taskById(args.id)
        }.doOnComplete {
            _uiState.update {
                it.copy(isLoading = false)
            }
        }.doOnSuccess { task ->
            _uiState.update {
                it.copy(task = task)
            }
        }.doOnError { _, _ ->
            _uiState.update {
                it.copy(
                    messageBannerState = MessageBannerState.defaultState(
                        onClick = ::onReloadClicked,
                    ),
                )
            }
        }
    }

    private fun onReloadClicked() {
        loadTask()
    }

    fun onPointIdChanged(pointId: String) {
        _uiState.update {
            it.copy(pointId = pointId)
        }
    }

    fun onStartButtonClicked() { /*todo*/ }

    fun onShareIconClicked() { /*todo*/ }

    fun onSaveButtonClicked() { /*todo*/ }

    fun onConnectButtonClicked() { /*todo*/ }

    fun onVerifyButtonClicked() { /*todo*/ }

    fun onPointsNotFoundButtonClicked() { /*todo*/ }

    data class UiState(
        val task: TaskModel? = null,
        val isLoading: Boolean = true,
        val messageBannerState: MessageBannerState? = null,
        val pointId: String = "",
    )

    sealed class Command
}