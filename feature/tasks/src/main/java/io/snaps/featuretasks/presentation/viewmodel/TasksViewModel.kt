package io.snaps.featuretasks.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basenft.data.NftRepository
import io.snaps.basenft.ui.CollectionItemState
import io.snaps.basenft.ui.toNftCollectionItemState
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.domain.QuestModel
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.featuretasks.data.TasksRepository
import io.snaps.featuretasks.presentation.HistoryTasksUiState
import io.snaps.featuretasks.presentation.energyProgress
import io.snaps.featuretasks.presentation.toHistoryTasksUiState
import io.snaps.featuretasks.presentation.toRemainingTimeTileState
import io.snaps.featuretasks.presentation.toTaskTileState
import io.snaps.featuretasks.presentation.ui.RemainingTimeTileState
import io.snaps.featuretasks.presentation.ui.TaskTileState
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
class TasksViewModel @Inject constructor(
    private val action: Action,
    private val mainHeaderHandler: MainHeaderHandler,
    private val profileRepository: ProfileRepository,
    private val tasksRepository: TasksRepository,
    private val nftRepository: NftRepository,
) : SimpleViewModel(), MainHeaderHandler by mainHeaderHandler {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        subscribeToCurrentQuests()
        subscribeToHistoryQuests()
        subscribeToUserNftCollection()
        loadCurrentTasks()
        loadUserNftCollection()
    }

    private fun subscribeToUserNftCollection() {
        nftRepository.nftCollectionState.onEach { state ->
            _uiState.update {
                it.copy(
                    userNftCollection = state.toNftCollectionItemState(
                        maxCount = 0,
                        onAddItemClicked = {},
                        onReloadClicked = ::onUserNftReloadClicked,
                    )
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToCurrentQuests() {
        profileRepository.currentQuestsState.onEach { state ->
            _uiState.update {
                it.copy(
                    current = state.toTaskTileState(
                        onReloadClicked = ::onCurrentReloadClicked,
                        onItemClicked = ::onCurrentTaskItemClicked,
                    ),
                    remainingTime = state.toRemainingTimeTileState(),
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToHistoryQuests() {
        tasksRepository.getHistoryTasksState().map { state ->
            _uiState.update {
                it.copy(
                    history = state.toHistoryTasksUiState(
                        shimmerListSize = 6,
                        onReloadClicked = ::onHistoryReloadClicked,
                        onListEndReaching = ::onListEndReaching,
                        onItemClicked = ::onHistoryTaskItemClicked,
                    )
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun onHistoryReloadClicked() = viewModelScope.launch {
        action.execute {
            tasksRepository.refreshHistoryTasks()
        }
    }

    private fun loadCurrentTasks() = viewModelScope.launch {
        action.execute {
            profileRepository.updateData()
        }
    }

    private fun loadUserNftCollection() = viewModelScope.launch {
        action.execute {
            nftRepository.updateNftCollection()
        }
    }

    private fun onCurrentReloadClicked() {
        loadCurrentTasks()
    }

    private fun onUserNftReloadClicked() {
        loadUserNftCollection()
    }

    private fun onListEndReaching() = viewModelScope.launch {
        action.execute {
            tasksRepository.loadNextHistoryTaskPage()
        }
    }

    private fun onCurrentTaskItemClicked(quest: QuestModel) = viewModelScope.launch {
        _command publish Command.OpenTaskDetailsScreen(
            AppRoute.TaskDetails.Args(
                type = quest.type,
                energy = quest.energy,
                energyProgress = quest.energyProgress(),
                completed = quest.energyProgress() == quest.energy,
            )
        )
    }

    private fun onHistoryTaskItemClicked(task: QuestModel) = viewModelScope.launch {
        onCurrentTaskItemClicked(task)
    }

    data class UiState(
        val current: List<TaskTileState> = List(6) { TaskTileState.Shimmer },
        val history: HistoryTasksUiState = HistoryTasksUiState(),
        val remainingTime: RemainingTimeTileState = RemainingTimeTileState.Shimmer,
        val userNftCollection: List<CollectionItemState> = List(6) { CollectionItemState.Shimmer },
    )

    sealed class Command {
        data class OpenTaskDetailsScreen(val args: AppRoute.TaskDetails.Args) : Command()
    }
}