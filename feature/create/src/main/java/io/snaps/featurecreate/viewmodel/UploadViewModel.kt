package io.snaps.featurecreate.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.basefile.data.FileRepository
import io.snaps.basefile.domain.FileModel
import io.snaps.basesources.NotificationsSource
import io.snaps.basesources.UploadStatusSource
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.FileManager
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import kotlinx.coroutines.Job
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
class UploadViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val action: Action,
    private val notificationsSource: NotificationsSource,
    private val uploadStatusSource: UploadStatusSource,
    private val fileManager: FileManager,
    private val fileRepository: FileRepository,
    private val videoFeedRepository: VideoFeedRepository,
) : SimpleViewModel() {

    private val args = savedStateHandle.requireArgs<AppRoute.PreviewVideo.Args>()

    private val _uiState = MutableStateFlow(UiState(args.uri))
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private var progressListenJob: Job? = null

    fun onPublishClicked(thumbnail: Bitmap?) {
        thumbnail ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val thumbnailFile = fileManager.createFileFromBitmap(thumbnail) ?: run {
                log("Couldn't create a file for thumbnail")
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }
            action.execute {
                fileRepository.uploadFile(thumbnailFile)
            }.doOnSuccess { fileModel ->
                uploadVideo(fileModel, uiState.value.uri)
            }.doOnError { _, _ ->
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun uploadVideo(fileModel: FileModel, filePath: String) {
        action.execute {
            videoFeedRepository.uploadVideo(
                title = _uiState.value.titleValue.trim(),
                fileId = fileModel.id,
                filePath = filePath,
            )
        }.doOnSuccess {
            startProgressListen(it)
        }.doOnComplete {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun startProgressListen(uploadId: Uuid) {
        progressListenJob?.cancel()
        progressListenJob = uploadStatusSource.listenTo(uploadId).onEach { state ->
            when (state) {
                is UploadStatusSource.State.Error -> {
                    _uiState.update { it.copy(uploadingProgress = null) }
                    notificationsSource.sendError(state.error)
                }
                is UploadStatusSource.State.Progress -> {
                    _uiState.update { it.copy(uploadingProgress = state.progress / 100f) }
                }
                is UploadStatusSource.State.Success -> {
                    videoFeedRepository.refreshFeed(VideoFeedType.User(null))
                    notificationsSource.sendMessage(StringKey.PreviewVideoMessageSuccess.textValue())
                    _command publish Command.CloseScreen
                }
                null -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun onTitleChanged(value: String) {
        _uiState.update {
            it.copy(titleValue = value)
        }
    }

    fun onDescriptionChanged(value: String) {
        _uiState.update {
            it.copy(descriptionValue = value)
        }
    }

    data class UiState(
        val uri: String,
        val isLoading: Boolean = false,
        val uploadingProgress: Float? = null,
        val titleValue: String = "",
        val descriptionValue: String = "",
    ) {

        val isPublishEnabled = titleValue.isNotBlank() && uploadingProgress == null
    }

    sealed class Command {
        object CloseScreen : Command()
    }
}