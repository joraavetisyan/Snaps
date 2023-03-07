package io.snaps.featurecreate.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefile.data.FileRepository
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.FileManager
import io.snaps.coreui.viewmodel.SimpleViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val action: Action,
    private val fileRepository: FileRepository,
    private val videoFeedRepository: VideoFeedRepository,
    private val fileManager: FileManager,
) : SimpleViewModel() {

    private val args = savedStateHandle.requireArgs<AppRoute.PreviewVideo.Args>()

    private val _uiState = MutableStateFlow(UiState(args.uri))
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onProceedClicked() = viewModelScope.launch {
        val file = fileManager.createFileFromUri(
            Uri.parse(uiState.value.uri)
        ) ?: return@launch
        action.execute {
            fileRepository.uploadFile(file)
        }.doOnSuccess {
            // todo
            videoFeedRepository.addVideo(
                title = "title",
                description = "description",
                fileId = it.id,
            )
        }
    }

    data class UiState(
        val uri: String,
    )

    sealed class Command
}