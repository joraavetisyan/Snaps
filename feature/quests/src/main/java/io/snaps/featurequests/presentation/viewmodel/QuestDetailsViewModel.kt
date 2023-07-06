package io.snaps.featurequests.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.ui.CreateCheckHandler
import io.snaps.corecommon.model.QuestType
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.featurequests.presentation.taskDefaultCount
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    createCheckHandler: CreateCheckHandler,
) : SimpleViewModel(), CreateCheckHandler by createCheckHandler {

    private val args = savedStateHandle.requireArgs<AppRoute.QuestDetails.Args>()

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
            QuestType.Like -> Command.OpenMainVideoFeed
            QuestType.PublishVideo -> {
                tryOpenCreate()
                null
            }
            QuestType.SocialPost -> Command.OpenShareTemplate
            QuestType.SocialShare -> Command.OpenMainVideoFeed
            QuestType.Subscribe -> Command.OpenMainVideoFeed
            QuestType.Watch -> Command.OpenMainVideoFeed
        }?.let {
            _command publish it
        }
    }

    data class UiState(
        val type: QuestType,
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
    }
}