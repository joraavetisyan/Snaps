package io.snaps.basefeed.ui

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import io.snaps.basefeed.data.CommentRepository
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.domain.VideoFeedPageModel
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.baseplayer.domain.VideoClipModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesources.BottomBarVisibilitySource
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.model.SocialNetwork
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.Action
import io.snaps.coredata.source.AppModel
import io.snaps.coredata.source.GetInstalledAppListUseCase
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

abstract class VideoFeedViewModel(
    private val videoFeedType: VideoFeedType,
    private val action: Action,
    private val videoFeedRepository: VideoFeedRepository,
    private val profileRepository: ProfileRepository,
    private val commentRepository: CommentRepository,
    private val bottomBarVisibilitySource: BottomBarVisibilitySource,
    private val getInstalledAppListUseCase: GetInstalledAppListUseCase,
    val startPosition: Int = 0,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private var commentsLoadJob: Job? = null
    private var authorLoadJob: Job? = null
    private var loaded: Boolean = false

    private var videoFeedPageModel: VideoFeedPageModel? = null

    init {
        _uiState.update { state ->
            state.copy(
                shareDialogItems = getInstalledAppListUseCase.invoke(
                    SocialNetwork.values().map { it.url }.toList()
                )
            )
        }
        subscribeToProfile()
        subscribeOnVideoFeed()
    }

    private fun subscribeToProfile() {
        profileRepository.state.onEach { profileState ->
            _uiState.update { it.copy(profileAvatar = profileState.dataOrCache?.avatar) }
        }.launchIn(viewModelScope)
    }

    private fun subscribeOnVideoFeed() {
        videoFeedRepository.getFeedState(videoFeedType).map {
            videoFeedPageModel = it
            it.toVideoFeedUiState(
                shimmerListSize = 1,
                onClipClicked = ::onClipClicked,
                onReloadClicked = ::onReloadClicked,
                onListEndReaching = ::onListEndReaching,
            )
        }.onEach { state ->
            _uiState.update { it.copy(videoFeedUiState = state) }
            if (state.isData && startPosition == 0 && !loaded) {
                loaded = true
                onScrolledToPosition(0)
            }
        }.launchIn(viewModelScope)
    }

    private fun onClipClicked(clip: VideoClipModel) {}

    private fun onReloadClicked() {
        viewModelScope.launch { action.execute { videoFeedRepository.refreshFeed(videoFeedType) } }
    }

    private fun onListEndReaching() {
        viewModelScope.launch { action.execute { videoFeedRepository.loadNextFeedPage(videoFeedType) } }
    }

    fun onScrolledToPosition(position: Int) {
        val current =
            _uiState.value.videoFeedUiState.items.getOrNull(position) as? VideoClipUiState.Data
        val videoClip = current?.clip ?: return
        commentsLoadJob?.cancel()
        commentsLoadJob = commentRepository.getCommentsState(videoClip.id).map {
            it.toCommentsUiState(
                shimmerListSize = 10,
                onClipClicked = {},
                onReloadClicked = {},
                onListEndReaching = { onCommentListEndReaching(videoClip.id) },
            )
        }.onEach { state ->
            _uiState.update { it.copy(commentsUiState = state) }
        }.launchIn(viewModelScope)
        authorLoadJob?.cancel()
        authorLoadJob = viewModelScope.launch {
            action.execute {
                profileRepository.getUserInfoById(videoClip.authorId)
            }.doOnSuccess { profileModel ->
                if (!isActive) return@doOnSuccess
                _uiState.update {
                    it.copy(authorProfileAvatar = profileModel.avatar)
                }
            }
        }
    }

    private fun onCommentListEndReaching(videoId: Uuid) {
        viewModelScope.launch { action.execute { commentRepository.loadNextCommentPage(videoId) } }
    }

    fun onBottomSheetHidden() {
        bottomBarVisibilitySource.updateState(true)
    }

    fun onCommentInputBottomSheetHidden() {
        onCommentChanged(TextFieldValue(""))
    }

    fun onMuteClicked(isMuted: Boolean) {
        _uiState.update { it.copy(isMuted = isMuted) }
    }

    fun onAuthorClicked(clipModel: VideoClipModel) {
        viewModelScope.launch { _command publish Command.OpenProfileScreen(clipModel.authorId) }
    }

    fun onLikeClicked(clipModel: VideoClipModel) = viewModelScope.launch {
        likeVideoClip(clipModel)
        action.execute {
            videoFeedRepository.like(clipModel.id)
        }
    }

    private fun likeVideoClip(clipModel: VideoClipModel) {
        val videoClips = videoFeedPageModel?.loadedPageItems?.map {
            when (it.id) {
                clipModel.id -> it.copy(
                    isLiked = !clipModel.isLiked,
                    likeCount = clipModel.likeCount + 1,
                )
                else -> it
            }
        } ?: emptyList()

        videoFeedPageModel = videoFeedPageModel?.copy(loadedPageItems = videoClips)
        _uiState.update {
            it.copy(
                videoFeedUiState = videoFeedPageModel?.toVideoFeedUiState(
                    shimmerListSize = 1,
                    onClipClicked = ::onClipClicked,
                    onReloadClicked = ::onReloadClicked,
                    onListEndReaching = ::onListEndReaching,
                ) ?: VideoFeedUiState(),
            )
        }
    }
    fun onCommentClicked(clipModel: VideoClipModel) {
        bottomBarVisibilitySource.updateState(false)
        _uiState.update {
            it.copy(bottomDialogType = BottomDialogType.Comments)
        }
        viewModelScope.launch { _command publish Command.ShowBottomDialog }
    }

    fun onCommentChanged(newValue: TextFieldValue) {
        _uiState.update { it.copy(comment = newValue) }
    }

    fun onCommentInputClick() {
        viewModelScope.launch { _command publish Command.ShowCommentInputBottomDialog }
    }

    fun onCommentSendClick() {
        val comment = uiState.value.commentsUiState.items.firstOrNull() as? CommentUiState.Data ?: return
        viewModelScope.launch {
            action.execute {
                commentRepository.createComment(
                    videoId = comment.item.videoId,
                    text = uiState.value.comment.text,
                ).doOnSuccess {
                    _command publish Command.HideCommentInputBottomDialog
                    _uiState.update { it.copy(comment = TextFieldValue("")) }
                }
            }
        }
    }

    fun onEmojiClicked(emoji: String) {
        _uiState.update {
            val newText = it.comment.text + emoji
            it.copy(comment = TextFieldValue(text = newText, selection = TextRange(newText.length)))
        }
    }

    fun onShareClicked(clipModel: VideoClipModel) = viewModelScope.launch {
        bottomBarVisibilitySource.updateState(false)
        _uiState.update {
            it.copy(bottomDialogType = BottomDialogType.Share(clipModel.url))
        }
        _command publish Command.ShowBottomDialog
    }

    fun onShareDialogItemClicked(packageName: String) = viewModelScope.launch {
        _command publish Command.HideBottomDialog
        val socialNetwork = SocialNetwork.values().first {
            packageName.contains(it.url)
        }
        action.execute {
            videoFeedRepository.shareInfo(socialNetwork)
        }
    }

    data class UiState(
        val isMuted: Boolean = false,
        val profileAvatar: ImageValue? = null,
        val authorProfileAvatar: ImageValue? = null,
        val comment: TextFieldValue = TextFieldValue(""),
        val videoFeedUiState: VideoFeedUiState = VideoFeedUiState(),
        val commentsUiState: CommentsUiState = CommentsUiState(),
        val shareDialogItems: List<AppModel> = emptyList(),
        val bottomDialogType: BottomDialogType = BottomDialogType.Comments,
    ) {

        val commentListSize = if (commentsUiState.items.none { it is CommentUiState.Shimmer }) {
            // -1 to not count the progress indicator
            (commentsUiState.items.size - 1).coerceAtLeast(0)
        } else 0
    }

    sealed class BottomDialogType {
        object Comments : BottomDialogType()
        data class Share(val url: String) : BottomDialogType()
    }

    sealed class Command {
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
        object ShowCommentInputBottomDialog : Command()
        object HideCommentInputBottomDialog : Command()
        data class ScrollToPosition(val position: Int) : Command()
        data class OpenProfileScreen(val userId: Uuid) : Command()
    }
}