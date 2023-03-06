package io.snaps.featuretasks.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.corecommon.model.QuestType
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.featuretasks.data.TasksRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val action: Action,
    private val tasksRepository: TasksRepository,
) : SimpleViewModel() {

    private val args = savedStateHandle.requireArgs<AppRoute.TaskDetails.Args>()

    private val _uiState = MutableStateFlow(
        UiState(
            type = args.type,
            energy = args.energy,
            energyProgress = args.energyProgress,
            completed = args.completed
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onStartButtonClicked() = viewModelScope.launch {
        val command = when (args.type) {
            QuestType.Like -> Command.OpenMainVideoFeed
            QuestType.PublishVideo -> Command.OpenCreateVideo
            QuestType.SocialPost -> Command.OpenShareTemplate
            QuestType.SocialShare -> Command.OpenMainVideoFeed
            QuestType.Subscribe -> Command.OpenMainVideoFeed
            QuestType.Watch -> Command.OpenMainVideoFeed
        }
        _command publish command
    }

    fun onPointIdChanged(pointId: String) {
        _uiState.update {
            it.copy(pointId = pointId)
        }
    }

    fun onShareIconClicked() { /*todo*/ }

    fun onSaveButtonClicked() { /*todo*/ }

    fun onConnectButtonClicked() { /*todo*/ }

    fun onVerifyButtonClicked() { /*todo*/ }

    fun onPointsNotFoundButtonClicked() { /*todo*/ }

    data class UiState(
        val type: QuestType,
        val energy: Int,
        val energyProgress: Int,
        val completed: Boolean,
        val isLoading: Boolean = false,
        val messageBannerState: MessageBannerState? = null,
        val pointId: String = "",
    )

    sealed class Command {
        object OpenMainVideoFeed : Command()
        object OpenShareTemplate : Command()
        object OpenCreateVideo : Command()
    }
}