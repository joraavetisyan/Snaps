package io.snaps.featuretasks.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesources.NotificationsSource
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.TaskType
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.featuretasks.data.TasksRepository
import io.snaps.featuretasks.presentation.taskDefaultCount
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
    private val notificationsSource: NotificationsSource,
    @Bridged private val profileRepository: ProfileRepository,
    @Bridged private val videoFeedRepository: VideoFeedRepository,
) : SimpleViewModel() {

    private val args = savedStateHandle.requireArgs<AppRoute.TaskDetails.Args>()

    private val _uiState = MutableStateFlow(
        UiState(
            type = args.type,
            energy = args.energy,
            count = (args.count ?: taskDefaultCount(args.type)).toString(),
            energyProgress = args.energyProgress,
            completed = args.completed,
        )
    )

    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onStartButtonClicked() = viewModelScope.launch {
        when (args.type) {
            TaskType.Like -> Command.OpenMainVideoFeed
            TaskType.PublishVideo -> {
                val (isAllowed, maxCount) = videoFeedRepository.isAllowedToCreate(profileRepository.state.value.dataOrCache)
                if (isAllowed) {
                    Command.OpenCreateVideo
                } else {
                    notificationsSource.sendError(StringKey.ErrorCreateVideoLimit.textValue(maxCount.toString()))
                    null
                }
            }
            TaskType.SocialPost -> Command.OpenShareTemplate
            TaskType.SocialShare -> Command.OpenMainVideoFeed
            TaskType.Subscribe -> Command.OpenMainVideoFeed
            TaskType.Watch -> Command.OpenMainVideoFeed
        }?.let {
            _command publish it
        }
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
        val type: TaskType,
        val energy: Int,
        val count: String,
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