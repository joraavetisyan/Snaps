package io.snaps.featurefeed.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseplayer.domain.VideoClipModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.coreui.viewmodel.SimpleViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class PopularVideosViewModel @Inject constructor(
    mainHeaderHandlerDelegate: MainHeaderHandler,
) : SimpleViewModel(), MainHeaderHandler by mainHeaderHandlerDelegate {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    data class UiState(
        val videoClipModels: List<VideoClipModel> = emptyList(),
    )

    sealed class Command
}