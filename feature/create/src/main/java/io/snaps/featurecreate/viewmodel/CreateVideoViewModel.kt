package io.snaps.featurecreate.viewmodel

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basesources.NotificationsSource
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.FileManager
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.nanoseconds

@Suppress("EnumEntryName")
enum class RecordTiming(val seconds: Int) {
    _120(120),
    _60(60),
    _15(15),
    ;
}

@Suppress("EnumEntryName")
enum class RecordDelay(val seconds: Int) {
    _3(3),
    _5(5),
    _10(10),
    _0(0),
    ;
}

@HiltViewModel
class CreateVideoViewModel @Inject constructor(
    private val fileManager: FileManager,
    private val notificationsSource: NotificationsSource,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private var delayTimerJob: Job? = null

    fun onTimingSelected(recordTiming: RecordTiming) {
        _uiState.update { it.copy(selectedRecordTiming = recordTiming) }
    }

    fun onRecordDelaySelected(delay: RecordDelay) {
        _uiState.update { it.copy(selectedDelay = delay) }
    }

    fun onRecordingStartClicked(
        onActualStart: (onRecordingStatusChanged: (isRecording: Boolean) -> Unit) -> Unit,
    ) {
        when (val delay = _uiState.value.selectedDelay) {
            RecordDelay._0 -> onActualStart { isRecording ->
                _uiState.update { it.copy(isRecording = isRecording, progress = 0f) }
            }
            else -> startDelayTimer(delay.seconds) {
                onActualStart { isRecording ->
                    _uiState.update {
                        it.copy(delayValue = null, isRecording = isRecording, progress = 0f)
                    }
                }
            }
        }
    }

    private fun startDelayTimer(start: Int, onTimerFinished: () -> Unit) {
        delayTimerJob?.cancel()
        delayTimerJob = viewModelScope.launch {
            var current = start
            while (isActive && current > 0) {
                _uiState.update { it.copy(delayValue = current.toString()) }
                delay(1000L)
                if (--current <= 0) {
                    onTimerFinished()
                }
            }
        }
    }

    fun onDelayCanceled() {
        delayTimerJob?.cancel()
        _uiState.update { it.copy(delayValue = null) }
    }

    fun onCameraChanged(isFrontCamera: Boolean) {
        _uiState.update { it.copy(isFrontCamera = isFrontCamera) }
    }

    fun onRecorded(nanos: Long) {
        _uiState.update {
            it.copy(
                progress = nanos.nanoseconds.inWholeSeconds.toFloat() / it.selectedRecordTiming.seconds,
            )
        }
    }

    fun onVideoSelected(uri: Uri) {
        val videoUri = fileManager.createFileFromUri(uri)?.toUri() ?: return
        val duration = fileManager.getMediaDuration(videoUri)?.inWholeSeconds?.toInt()
        viewModelScope.launch {
            if (duration != null && duration > RecordTiming._120.seconds) {
                notificationsSource.sendError(StringKey.CreateVideoMessageDurationLimit.textValue())
            } else {
                videoUri.path?.let { filePath ->
                    _command publish Command.OpenPreviewScreen(filePath)
                }
            }
        }
    }

    data class UiState(
        val progress: Float = 0f,
        val isRecording: Boolean = false,
        val isFrontCamera: Boolean = false,
        val selectedDelay: RecordDelay = RecordDelay._0,
        val delayValue: String? = null,
        val selectedRecordTiming: RecordTiming = RecordTiming._120,
    ) {

        fun isSelected(recordTiming: RecordTiming) = selectedRecordTiming == recordTiming
    }

    sealed class Command {
        data class OpenPreviewScreen(val uri: String) : Command()
    }
}