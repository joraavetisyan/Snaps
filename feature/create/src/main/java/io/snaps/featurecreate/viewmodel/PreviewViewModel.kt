package io.snaps.featurecreate.viewmodel

import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.basefile.data.FileRepository
import io.snaps.basefile.domain.FileModel
import io.snaps.basesources.NotificationsSource
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.FileManager
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val action: Action,
    private val fileRepository: FileRepository,
    private val videoFeedRepository: VideoFeedRepository,
    private val fileManager: FileManager,
    private val notificationsSource: NotificationsSource,
) : SimpleViewModel() {

    private val args = savedStateHandle.requireArgs<AppRoute.PreviewVideo.Args>()

    private val _uiState = MutableStateFlow(UiState(args.uri))
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onProceedClicked() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        val thumbnail: Bitmap = ThumbnailUtils.createVideoThumbnail(
            uiState.value.uri,
            MediaStore.Images.Thumbnails.MINI_KIND,
        ) ?: run {
            log("Couldn't create a thumbnail")
            _uiState.update { it.copy(isLoading = false) }
            return@launch
        }
        val thumbnailFile = fileManager.createFileFromBitmap(thumbnail) ?: run {
            log("Couldn't create a file for thumbnail")
            _uiState.update { it.copy(isLoading = false) }
            return@launch
        }
        action.execute {
            fileRepository.uploadFile(thumbnailFile)
        }.doOnSuccess { fileModel ->
            addVideo(fileModel, Uri.parse(uiState.value.uri))
        }.doOnError { _, _ ->
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun addVideo(fileModel: FileModel, uri: Uri) {
        action.execute {
            videoFeedRepository.addVideo(
                title = _uiState.value.titleValue.trim(),
                description = _uiState.value.descriptionValue.trim(),
                fileId = fileModel.id,
            ).doOnSuccess {
                videoFeedRepository.uploadVideo(uri, it.id)
            }
        }.doOnSuccess {
            videoFeedRepository.refreshFeed(VideoFeedType.User(null))
            notificationsSource.sendMessage(StringKey.PreviewVideoMessageSuccess.textValue())
            _command publish Command.CloseScreen
        }.doOnComplete {
            _uiState.update { it.copy(isLoading = false) }
        }
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
        val titleValue: String = "",
        val descriptionValue: String = "",
    ) {

        val isProceedEnabled = titleValue.isNotBlank() && descriptionValue.isNotBlank()
    }

    sealed class Command {
        object CloseScreen : Command()
    }
}