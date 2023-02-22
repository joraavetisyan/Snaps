package io.snaps.featurecreate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.coreui.viewmodel.SimpleViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.time.Duration.Companion.nanoseconds

@Suppress("EnumEntryName")
enum class Timing(val seconds: Int) {
    _180(180),
    _60(60),
    _15(15),
    ;
}

@HiltViewModel
class CreateVideoViewModel @Inject constructor(
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onTimingSelected(timing: Timing) {
        _uiState.update { it.copy(selectedTiming = timing) }
    }

    fun onRecordingClicked(isRecording: Boolean) {
        _uiState.update { it.copy(isRecording = isRecording, progress = 0f) }
    }

    fun onCameraChanged(isFrontCamera: Boolean) {
        _uiState.update { it.copy(isFrontCamera = isFrontCamera) }
    }

    fun onRecorded(nanos: Long) {
        _uiState.update {
            it.copy(
                progress = nanos.nanoseconds.inWholeSeconds.toFloat() / it.selectedTiming.seconds,
            )
        }
    }

    data class UiState(
        val progress: Float = 0f,
        val isRecording: Boolean = false,
        val isFrontCamera: Boolean = false,
        val selectedTiming: Timing = Timing._180,
    ) {

        fun isSelected(timing: Timing) = selectedTiming == timing
    }

    sealed class Command
}