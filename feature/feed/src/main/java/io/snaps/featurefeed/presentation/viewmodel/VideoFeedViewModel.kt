package io.snaps.featurefeed.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.ui.VideoFeedUiState
import io.snaps.basefeed.ui.toVideoFeedUiState
import io.snaps.baseplayer.domain.VideoClipModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoFeedViewModel @Inject constructor(
    mainHeaderHandlerDelegate: MainHeaderHandler,
    private val action: Action,
    private val videoFeedRepository: VideoFeedRepository,
) : SimpleViewModel(), MainHeaderHandler by mainHeaderHandlerDelegate {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        subscribeOnVideoFeed()
    }

    private fun subscribeOnVideoFeed() {
        videoFeedRepository.getFeedState().map {
            it.toVideoFeedUiState(
                shimmerListSize = 1,
                onClipClicked = ::onClipClicked,
                onReloadClicked = ::onReloadClicked,
                onListEndReaching = ::onListEndReaching,
            )
        }.onEach { state ->
            _uiState.update { it.copy(videoFeedUiState = state) }
        }.launchIn(viewModelScope)
    }

    private fun onClipClicked(clip: VideoClipModel) {}

    private fun onReloadClicked() {}

    private fun onListEndReaching() {
        viewModelScope.launch { action.execute { videoFeedRepository.loadNextFeedPage() } }
    }

    fun onMuteClicked(isMuted: Boolean) {
        _uiState.update {
            it.copy(isMuted = isMuted)
        }
    }

    fun onAuthorClicked(clipModel: VideoClipModel) {
    }

    fun onLikeClicked(clipModel: VideoClipModel) {
    }

    fun onCommentClicked(clipModel: VideoClipModel) {
    }

    fun onShareClicked(clipModel: VideoClipModel) {
    }

    data class UiState(
        val isMuted: Boolean = false,
        val videoFeedUiState: VideoFeedUiState = VideoFeedUiState(),
    )

    sealed class Command
}