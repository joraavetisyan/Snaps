package io.snaps.featurecreate.viewmodel

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_DURATION
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefile.data.FileRepository
import io.snaps.basefile.domain.FileModel
import io.snaps.basesources.NotificationsSource
import io.snaps.basefeed.data.UploadStatusSource
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.logE
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.FileManager
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import kotlinx.coroutines.CoroutineDispatcher
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
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle,
    private val action: Action,
    private val notificationsSource: NotificationsSource,
    private val uploadStatusSource: UploadStatusSource,
    private val fileManager: FileManager,
    private val fileRepository: FileRepository,
    private val videoCompressor: VideoCompressor,
    @Bridged private val profileRepository: ProfileRepository,
    @Bridged private val videoFeedRepository: VideoFeedRepository,
) : SimpleViewModel() {

    private val args = savedStateHandle.requireArgs<AppRoute.UploadVideo.Args>()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private var progressListenJob: Job? = null

    init {
        viewModelScope.launch(ioDispatcher) {
            val retriever = MediaMetadataRetriever().apply { setDataSource(args.uri) }
            val durationMillis = retriever.extractMetadata(METADATA_KEY_DURATION)?.toLong() ?: 0L
            val visibleFrameCount = 6 // visible on screen
            val frameDuration = durationMillis / (3 * visibleFrameCount)
            val frameCount = if (frameDuration == 0L) 1 else {
                (durationMillis / frameDuration).toInt().coerceAtLeast(1)
            }
            val bitmaps = List(frameCount) { frame ->
                retriever.getFrameAtTime(
                    frame * frameDuration * 1000L, // micros
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC,
                )
            }
            retriever.release()
            _uiState.update {
                it.copy(
                    isRetrievingBitmaps = false,
                    visibleFrameCount = visibleFrameCount,
                    frameCount = frameCount,
                    bitmaps = bitmaps,
                )
            }
        }
    }

    fun onPublishClicked(thumbnail: Bitmap?) {
        thumbnail ?: return

        _uiState.update { it.copy(isLoading = true) }

        val thumbnailFile = fileManager.createFileFromBitmap(thumbnail)
        if (thumbnailFile == null) {
            logE("Couldn't create a file for thumbnail")
            _uiState.update { it.copy(isLoading = false) }
            return
        }

        fun load(videoPath: String) {
            viewModelScope.launch {
                action.execute {
                    fileRepository.uploadFile(thumbnailFile)
                }.doOnSuccess { fileModel ->
                    uploadVideo(thumbnail = fileModel, filePath = videoPath)
                }.doOnError { _, _ ->
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }

        if (videoCompressor.shouldCompress(args.uri)) {
            videoCompressor.compress(
                uri = args.uri,
                onFailure = {
                    viewModelScope.launch {
                        notificationsSource.sendError(StringKey.ErrorUnknown.textValue())
                        _uiState.update { it.copy(isLoading = false) }
                    }
                },
                onSuccess = ::load,
            )
        } else {
            load(args.uri)
        }
    }

    private suspend fun uploadVideo(thumbnail: FileModel, filePath: String) {
        action.execute {
            videoFeedRepository.upload(
                title = _uiState.value.titleValue.trim(),
                thumbnailFileId = thumbnail.id,
                file = filePath,
                userInfoModel = profileRepository.state.value.dataOrCache,
            )
        }.doOnSuccess {
            startProgressListen(it)
        }.doOnComplete {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun startProgressListen(uploadId: Uuid) {
        progressListenJob?.cancel()
        progressListenJob = uploadStatusSource.listenToByUploadId(uploadId).onEach { state ->
            when (state) {
                is UploadStatusSource.State.Error -> {
                    _uiState.update { it.copy(uploadingProgress = null) }
                    notificationsSource.sendError(state.error)
                }
                is UploadStatusSource.State.Progress -> {
                    _uiState.update { it.copy(uploadingProgress = state.progress) }
                }
                is UploadStatusSource.State.Success -> {
                    notificationsSource.sendMessage(StringKey.MessageVideoUploadSuccess.textValue())
                    _command publish Command.CloseScreen
                }
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
        val isRetrievingBitmaps: Boolean = true,
        val visibleFrameCount: Int = 0,
        val frameCount: Int = 0,
        val bitmaps: List<Bitmap?> = emptyList(),
        val isLoading: Boolean = false,
        val uploadingProgress: Float? = null,
        val titleValue: String = "",
        val descriptionValue: String = "",
    ) {

        val isPublishEnabled = titleValue.isNotBlank() && uploadingProgress == null

        fun getBitmap(frame: Int): Bitmap? {
            return bitmaps[frame] ?: bitmaps[frameCount - 1]
        }
    }

    sealed class Command {
        object CloseScreen : Command()
    }
}