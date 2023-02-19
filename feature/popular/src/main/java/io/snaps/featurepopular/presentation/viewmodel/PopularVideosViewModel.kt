package io.snaps.featurepopular.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.basefeed.ui.VideoFeedUiState
import io.snaps.basefeed.ui.toVideoFeedUiState
import io.snaps.baseplayer.domain.VideoClipModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PopularVideosViewModel @Inject constructor(
    mainHeaderHandlerDelegate: MainHeaderHandler,
    private val videoFeedRepository: VideoFeedRepository,
    private val action: Action,
) : SimpleViewModel(), MainHeaderHandler by mainHeaderHandlerDelegate {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private var subscribeJob: Job? = null
    private var searchJob: Job? = null

    init {
        search("")
    }

    private fun search(query: String) {
        subscribeJob?.cancel()
        subscribeJob = videoFeedRepository.getFeedState(VideoFeedType.Popular(query)).map {
            it.toVideoFeedUiState(
                shimmerListSize = 6,
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
        viewModelScope.launch {
            action.execute {
                videoFeedRepository.loadNextFeedPage(VideoFeedType.Popular(_uiState.value.query))
            }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            if (!isActive) return@launch
            search(newQuery)
        }
    }

    fun onItemClicked(position: Int) {
        viewModelScope.launch {
            _command publish Command.OpenPopularVideoFeedScreen(
                query = _uiState.value.query,
                position = position,
            )
        }
    }

    data class UiState(
        val query: String = "",
        val videoFeedUiState: VideoFeedUiState = VideoFeedUiState(),
    )

    sealed class Command {
        data class OpenPopularVideoFeedScreen(val query: String, val position: Int) : Command()
    }
}