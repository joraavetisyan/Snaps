package io.snaps.featurecreate.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.config.AppSpecificStorageConfiguration
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.basefile.data.FileRepository
import io.snaps.basefile.domain.FileModel
import io.snaps.basesources.NotificationsSource
import io.snaps.basefeed.data.UploadStatusSource
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.log
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
import java.io.File
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
    @Bridged private val videoFeedRepository: VideoFeedRepository,
) : SimpleViewModel() {

    private val args = savedStateHandle.requireArgs<AppRoute.PreviewVideo.Args>()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private val retriever = MediaMetadataRetriever().apply { setDataSource(args.uri) }
    private var progressListenJob: Job? = null

    init {
        viewModelScope.launch(ioDispatcher) {
            val durationMillis = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
            val visibleFrameCount = 6 // visible on screen
            val frameDuration = durationMillis / (3 * visibleFrameCount)
            val frameCount = (durationMillis / frameDuration).toInt().coerceAtLeast(1)
            val bitmaps = List(frameCount) { frame ->
                retriever.getFrameAtTime(
                    frame * frameDuration * 1000L, // micros
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC,
                )
            }
            _uiState.update {
                it.copy(
                    isRetrievingBitmaps = false,
                    durationMillis = durationMillis,
                    visibleFrameCount = visibleFrameCount,
                    frameDuration = frameDuration,
                    frameCount = frameCount,
                    bitmaps = bitmaps,
                )
            }
        }
    }

    fun onPublishClicked(context: Context, thumbnail: Bitmap?) {
        thumbnail ?: return
        _uiState.update { it.copy(isLoading = true) }
        val thumbnailFile = fileManager.createFileFromBitmap(thumbnail) ?: run {
            log("Couldn't create a file for thumbnail")
            _uiState.update { it.copy(isLoading = false) }
            return
        }

        fun load(videoPath: String) = viewModelScope.launch {
            action.execute {
                fileRepository.uploadFile(thumbnailFile)
            }.doOnSuccess { fileModel ->
                uploadVideo(fileModel = fileModel, filePath = videoPath)
            }.doOnError { _, _ ->
                _uiState.update { it.copy(isLoading = false) }
            }
        }

        val sizeInMb = File(args.uri).length() / 1024 / 1024
        if (sizeInMb > 10) {
            fun onCompressFailure() = viewModelScope.launch {
                notificationsSource.sendError(StringKey.ErrorUnknown.textValue())
                _uiState.update { it.copy(isLoading = false) }
            }
            // todo behind interface
            VideoCompressor.start(
                context = context,
                uris = listOf(Uri.fromFile(File(args.uri))),
                configureWith = Configuration(
                    isMinBitrateCheckEnabled = false,
                ),
                appSpecificStorageConfiguration = AppSpecificStorageConfiguration(
                    videoName = "compressed_video",
                ),
                listener = object : CompressionListener {
                    override fun onCancelled(index: Int) {
                        log("Video compress cancelled")
                        onCompressFailure()
                    }

                    override fun onFailure(index: Int, failureMessage: String) {
                        log("Video compress failure: $failureMessage")
                        onCompressFailure()
                    }

                    override fun onProgress(index: Int, percent: Float) {
                    }

                    override fun onStart(index: Int) {
                    }

                    override fun onSuccess(index: Int, size: Long, path: String?) {
                        path ?: return
                        load(path)
                    }
                },
            )
        } else {
            load(args.uri)
        }
    }

    private suspend fun uploadVideo(fileModel: FileModel, filePath: String) {
        action.execute {
            videoFeedRepository.upload(
                title = _uiState.value.titleValue.trim(),
                fileId = fileModel.id,
                file = filePath,
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
                    action.execute {
                        videoFeedRepository.refreshFeed(VideoFeedType.User(null))
                    }.doOnComplete {
                        notificationsSource.sendMessage(StringKey.PreviewVideoMessageSuccess.textValue())
                        _command publish Command.CloseScreen
                    }
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
        val isRetrievingBitmaps: Boolean = true,
        val durationMillis: Long = 0L,
        val visibleFrameCount: Int = 0,
        val frameDuration: Long = 0L,
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