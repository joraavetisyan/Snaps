package io.snaps.featuretasks.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.data.model.QuestType
import io.snaps.baseprofile.domain.QuestModel
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.featuretasks.data.TasksRepository
import io.snaps.featuretasks.presentation.toTaskTileState
import io.snaps.featuretasks.presentation.ui.TaskTileState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val action: Action,
    private val profileRepository: ProfileRepository,
    private val tasksRepository: TasksRepository,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        subscribeToCurrentQuests()
        subscribeToHistoryQuests()
    }

    private fun subscribeToCurrentQuests() {
        profileRepository.currentQuestsState.onEach { state ->
            _uiState.update {
                it.copy(
                    current = state.toTaskTileState(
                        onReloadClicked = ::onHistoryReloadClicked,
                        onItemClicked = ::onItemClicked,
                    )
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToHistoryQuests() { // todo
        profileRepository.currentQuestsState.onEach { state ->
            _uiState.update {
                it.copy(
                    history = state.toTaskTileState(
                        onReloadClicked = ::onCurrentReloadClicked,
                        onItemClicked = ::onItemClicked,
                    )
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun onHistoryReloadClicked() = viewModelScope.launch { // todo
        action.execute {
            profileRepository.updateData()
        }
    }

    private fun onCurrentReloadClicked() = viewModelScope.launch {
        action.execute {
            profileRepository.updateData()
        }
    }

    private fun onItemClicked(quest: QuestModel) = viewModelScope.launch {
        val args = AppRoute.TaskArgs(
            energy = quest.energy,
            energyProgress = quest.energyProgress,
            completed = quest.completed,
        )
        val command = when (quest.type) {
            QuestType.Like -> Command.OpenLikeAndSubscribeTaskScreen(args)
            QuestType.PublishVideo -> Command.OpenPublishVideoTaskScreen(args)
            QuestType.SocialPost -> Command.OpenShareTaskScreen(args)
            QuestType.Subscribe -> Command.OpenLikeAndSubscribeTaskScreen(args)
            QuestType.SocialShare -> Command.OpenSocialShareTaskScreen(args)
            QuestType.Watch -> Command.OpenWatchVideoTaskScreen(args)
        }
        _command publish command
    }

    data class UiState(
        val current: List<TaskTileState> = List(6) { TaskTileState.Shimmer },
        val history: List<TaskTileState> = List(6) { TaskTileState.Shimmer },
    )

    sealed class Command {
        data class OpenFindPointsTaskScreen(val args: AppRoute.TaskArgs) : Command()
        data class OpenLikeAndSubscribeTaskScreen(val args: AppRoute.TaskArgs) : Command()
        data class OpenShareTaskScreen(val args: AppRoute.TaskArgs) : Command()
        data class OpenWatchVideoTaskScreen(val args: AppRoute.TaskArgs) : Command()
        data class OpenPublishVideoTaskScreen(val args: AppRoute.TaskArgs) : Command()
        data class OpenSocialShareTaskScreen(val args: AppRoute.TaskArgs) : Command()
    }
}