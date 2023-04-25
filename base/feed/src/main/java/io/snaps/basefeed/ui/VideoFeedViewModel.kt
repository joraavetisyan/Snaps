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
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.coreuicompose.uikit.other.ActionColor
import io.snaps.coreuicompose.uikit.other.ActionData
import io.snaps.coreuitheme.compose.AppTheme
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
    val startPosition: Int = 0,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(
        UiState(actions = getActions())
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private var commentsLoadJob: Job? = null
    private var authorLoadJob: Job? = null
    private var loaded: Boolean = false // to track load for the first item
    private var currentVideo: VideoClipModel? = null

    private var videoFeedPageModel: VideoFeedPageModel? = null

    init {
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
        val current = _uiState.value.videoFeedUiState.items.getOrNull(position) as? VideoClipUiState.Data
        val videoClip = current?.clip ?: return
        currentVideo = videoClip
        onViewed(videoClip)
        loadComments(videoClip.id)
        loadAuthor(videoClip.authorId)
    }

    private fun loadComments(videoId: Uuid) {
        commentsLoadJob?.cancel()
        commentsLoadJob = commentRepository.getCommentsState(videoId).map {
            it.toCommentsUiState(
                shimmerListSize = 10,
                onCommentClicked = {},
                onReloadClicked = {},
                onListEndReaching = { onCommentListEndReaching(videoId) },
            )
        }.onEach { state ->
            _uiState.update { it.copy(commentsUiState = state) }
        }.launchIn(viewModelScope)
    }

    private fun loadAuthor(authorId: Uuid) {
        authorLoadJob?.cancel()
        authorLoadJob = viewModelScope.launch {
            action.execute {
                profileRepository.getUserInfoById(authorId)
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

    private fun onViewed(clipModel: VideoClipModel) = viewModelScope.launch {
        action.execute(needProcessErrors = false) {
            videoFeedRepository.view(clipModel.id)
        }
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
                    likeCount = clipModel.likeCount + (if (clipModel.isLiked) -1 else 1),
                )
                else -> it
            }
        } ?: emptyList()

        videoFeedPageModel = videoFeedPageModel?.copy(loadedPageItems = videoClips)
        applyVideoClipToState()
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
        val video = currentVideo ?: return
        viewModelScope.launch {
            action.execute {
                commentRepository.createComment(
                    videoId = video.id,
                    text = uiState.value.comment.text.trim(),
                ).doOnSuccess {
                    _command publish Command.HideCommentInputBottomDialog
                    _uiState.update { it.copy(comment = TextFieldValue("")) }
                    refreshComments(video)
                    updateCommentCount(video)
                }
            }
        }
    }

    private fun updateCommentCount(video: VideoClipModel) {
        val videoClips = videoFeedPageModel?.loadedPageItems?.map {
            when (it.id) {
                video.id -> it.copy(
                    commentCount = video.commentCount + 1
                )
                else -> it
            }
        } ?: emptyList()

        videoFeedPageModel = videoFeedPageModel?.copy(loadedPageItems = videoClips)
        applyVideoClipToState()
    }

    private fun refreshComments(video: VideoClipModel) {
        viewModelScope.launch {
            action.execute { commentRepository.refreshComments(video.id) }
        }
    }

    private fun applyVideoClipToState() {
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

    fun onEmojiClicked(emoji: String) {
        _uiState.update {
            val newText = it.comment.text + emoji
            it.copy(comment = TextFieldValue(text = newText, selection = TextRange(newText.length)))
        }
    }

    fun onMoreClicked() = viewModelScope.launch {
        bottomBarVisibilitySource.updateState(false)
        _uiState.update {
            it.copy(bottomDialogType = BottomDialogType.MoreActions)
        }
        _command publish Command.ShowBottomDialog
    }

    private fun getActions(): List<ActionData> = listOfNotNull(
        ActionData(
            text = StringKey.VideoClipActionDelete.textValue(),
            icon = AppTheme.specificIcons.delete,
            color = ActionColor.Negative,
            onClick = { onDeleteClicked() },
        ).takeIf { videoFeedType is VideoFeedType.User },
    )

    private fun onDeleteClicked() = viewModelScope.launch {
        _command publish Command.HideBottomDialog
        bottomBarVisibilitySource.updateState(true)
        _uiState.update {
            it.copy(dialogType = DialogType.ConfirmDeleteVideo)
        }
    }

    fun onDeleteConfirmed() {
        val video = currentVideo ?: return
        viewModelScope.launch {
            action.execute {
                videoFeedRepository.deleteVideo(video.id)
            }.doOnSuccess {
                _uiState.update {
                    it.copy(dialogType = null)
                }
                videoFeedRepository.refreshFeed(videoFeedType)
                /*val videoClips = videoFeedPageModel?.loadedPageItems?.filter {
                    it.id != video.id
                }.orEmpty()
                videoFeedPageModel = videoFeedPageModel?.copy(loadedPageItems = videoClips)
                Log.e("videoClips", videoFeedPageModel?.loadedPageItems?.size.toString())
                applyVideoClipToState()*/
            }
        }
    }

    fun onDeleteDismissed() {
        _uiState.update {
            it.copy(dialogType = null)
        }
    }

    data class UiState(
        val isMuted: Boolean = false,
        val profileAvatar: ImageValue? = null,
        val authorProfileAvatar: ImageValue? = null,
        val comment: TextFieldValue = TextFieldValue(""),
        val videoFeedUiState: VideoFeedUiState = VideoFeedUiState(),
        val commentsUiState: CommentsUiState = CommentsUiState(),
        val bottomDialogType: BottomDialogType = BottomDialogType.Comments,
        val dialogType: DialogType? = null,
        val actions: List<ActionData>,
    ) {

        val commentListSize = commentsUiState.items.filterIsInstance<CommentUiState.Data>().size

        val isCommentSendEnabled get() = comment.text.isNotBlank()
    }

    sealed class BottomDialogType {
        object Comments : BottomDialogType()
        object MoreActions : BottomDialogType()
    }

    sealed class DialogType {
        object ConfirmDeleteVideo : DialogType()
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