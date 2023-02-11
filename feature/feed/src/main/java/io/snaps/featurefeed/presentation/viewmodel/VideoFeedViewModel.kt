package io.snaps.featurefeed.presentation.viewmodel

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.ui.VideoFeedUiState
import io.snaps.basefeed.ui.toVideoFeedUiState
import io.snaps.baseplayer.domain.VideoClipModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.corecommon.container.ImageValue
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.featurefeed.data.CommentRepository
import io.snaps.featurefeed.presentation.CommentUiState
import io.snaps.featurefeed.presentation.CommentsUiState
import io.snaps.featurefeed.presentation.toCommentsUiState
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
    private val profileRepository: ProfileRepository,
    private val commentRepository: CommentRepository,
) : SimpleViewModel(), MainHeaderHandler by mainHeaderHandlerDelegate {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        subscribeOnVideoFeed()
        subscribeToProfile()
        subscribeOnComments()
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

    private fun subscribeToProfile() {
        profileRepository.state.onEach { profileState ->
            _uiState.update { it.copy(profileAvatar = profileState.dataOrCache?.avatar) }
        }.launchIn(viewModelScope)
    }

    private fun subscribeOnComments() {
        commentRepository.getCommentsState("").map {
            it.toCommentsUiState(
                shimmerListSize = 10,
                onClipClicked = {},
                onReloadClicked = {},
                onListEndReaching = ::onCommentListEndReaching,
            )
        }.onEach { state ->
            _uiState.update { it.copy(commentsUiState = state) }
        }.launchIn(viewModelScope)
    }

    private fun onClipClicked(clip: VideoClipModel) {}

    private fun onReloadClicked() {}

    private fun onListEndReaching() {
        viewModelScope.launch { action.execute { videoFeedRepository.loadNextFeedPage() } }
    }

    private fun onCommentListEndReaching() {
        viewModelScope.launch { action.execute { commentRepository.loadNextCommentPage("") } }
    }

    fun onMuteClicked(isMuted: Boolean) {
        _uiState.update { it.copy(isMuted = isMuted) }
    }

    fun onAuthorClicked(clipModel: VideoClipModel) {
    }

    fun onLikeClicked(clipModel: VideoClipModel) {
    }

    fun onCommentClicked(clipModel: VideoClipModel) {
        viewModelScope.launch { _command publish Command.ShowBottomDialog }
    }

    fun onCommentChanged(newValue: TextFieldValue) {
        _uiState.update { it.copy(comment = newValue) }
    }

    fun onCommentInputClick() {
        viewModelScope.launch { _command publish Command.ShowCommentInputBottomDialog }
    }

    fun onCommentSendClick() {
        _uiState.update { it.copy(comment = TextFieldValue("")) }
        viewModelScope.launch { _command publish Command.HideCommentInputBottomDialog }
    }

    fun onEmojiClicked(emoji: String) {
        _uiState.update {
            val newText = it.comment.text + emoji
            it.copy(comment = TextFieldValue(text = newText, selection = TextRange(newText.length)))
        }
    }

    fun onShareClicked(clipModel: VideoClipModel) {
    }

    data class UiState(
        val isMuted: Boolean = false,
        val profileAvatar: ImageValue? = null,
        val comment: TextFieldValue = TextFieldValue(""),
        val videoFeedUiState: VideoFeedUiState = VideoFeedUiState(),
        val commentsUiState: CommentsUiState = CommentsUiState(),
    ) {

        val commentListSize = if (commentsUiState.items.none { it is CommentUiState.Shimmer }) {
            // -1 to not count the progress indicator
            (commentsUiState.items.size - 1).coerceAtLeast(0)
        } else 0
    }

    sealed class Command {
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
        object ShowCommentInputBottomDialog : Command()
        object HideCommentInputBottomDialog : Command()
    }
}