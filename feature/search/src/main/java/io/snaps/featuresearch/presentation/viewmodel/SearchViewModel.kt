package io.snaps.featuresearch.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.basefeed.ui.VideoFeedUiState
import io.snaps.basefeed.ui.toVideoFeedUiState
import io.snaps.baseplayer.domain.VideoClipModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.baseprofile.ui.UsersUiState
import io.snaps.baseprofile.ui.toUsersUiState
import io.snaps.corecommon.model.Uuid
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
class SearchViewModel @Inject constructor(
    mainHeaderHandlerDelegate: MainHeaderHandler,
    private val videoFeedRepository: VideoFeedRepository,
    private val profileRepository: ProfileRepository,
    private val action: Action,
) : SimpleViewModel(), MainHeaderHandler by mainHeaderHandlerDelegate {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private var subscribePopularFeedJob: Job? = null
    private var subscribeUsersJob: Job? = null
    private var searchJob: Job? = null

    init {
        search("")
    }

    private fun search(query: String) {
        subscribePopularFeedJob?.cancel()
        subscribeUsersJob?.cancel()
        subscribeOnUsers(query.trim())
        if (query.isBlank()) {
            subscribeOnPopularFeed()
        } else {
            subscribeOnAllVideo(query.trim())
        }
    }

    private fun subscribeOnPopularFeed() {
        subscribePopularFeedJob = videoFeedRepository.getFeedState(VideoFeedType.Popular).map {
            it.toVideoFeedUiState(
                shimmerListSize = 6,
                onClipClicked = ::onClipClicked,
                onReloadClicked = ::onPopularVideoReloadClicked,
                onListEndReaching = ::onPopularVideoListEndReaching,
            )
        }.onEach { state ->
            _uiState.update { it.copy(videoFeedUiState = state) }
        }.launchIn(viewModelScope)
    }

    private fun subscribeOnAllVideo(query: String) {
        subscribePopularFeedJob = videoFeedRepository.getFeedState(VideoFeedType.All(query)).map {
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

    private fun subscribeOnUsers(query: String) {
        subscribePopularFeedJob = profileRepository.getUsersState(query).map {
            it.toUsersUiState(
                shimmerListSize = 6,
                onUserClicked = ::onUserClicked,
                onReloadClicked = ::onUsersReloadClicked,
                onListEndReaching = ::onUsersListEndReaching,
            )
        }.onEach { state ->
            _uiState.update { it.copy(usersUiState = state) }
        }.launchIn(viewModelScope)
    }

    private fun onClipClicked(clip: VideoClipModel) {}

    private fun onUserClicked(user: UserInfoModel) = viewModelScope.launch {
        _command publish Command.OpenProfileScreen(user.userId)
    }

    private fun onUsersReloadClicked() = viewModelScope.launch {
        action.execute {
            profileRepository.refreshUsers(uiState.value.query)
        }
    }

    private fun onUsersListEndReaching() {
        viewModelScope.launch {
            action.execute {
                profileRepository.loadNextUsersPage(uiState.value.query)
            }
        }
    }

    private fun onPopularVideoReloadClicked() = viewModelScope.launch {
        action.execute {
            videoFeedRepository.refreshFeed(VideoFeedType.Popular)
        }
    }

    private fun onPopularVideoListEndReaching() {
        viewModelScope.launch {
            action.execute {
                videoFeedRepository.loadNextFeedPage(VideoFeedType.Popular)
            }
        }
    }

    private fun onReloadClicked() = viewModelScope.launch {
        action.execute {
            videoFeedRepository.refreshFeed(VideoFeedType.All(uiState.value.query))
        }
    }

    private fun onListEndReaching() {
        viewModelScope.launch {
            action.execute {
                videoFeedRepository.loadNextFeedPage(VideoFeedType.All(uiState.value.query))
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

    fun onClearQueryClicked() {
        onSearchQueryChanged("")
    }

    data class UiState(
        val query: String = "",
        val videoFeedUiState: VideoFeedUiState = VideoFeedUiState(),
        val usersUiState: UsersUiState = UsersUiState(),
    )

    sealed class Command {
        data class OpenPopularVideoFeedScreen(val query: String, val position: Int) : Command()
        data class OpenProfileScreen(val userId: Uuid) : Command()
    }
}