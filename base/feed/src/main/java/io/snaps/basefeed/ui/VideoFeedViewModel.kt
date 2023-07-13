package io.snaps.basefeed.ui

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import io.snaps.basefeed.data.CommentRepository
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.domain.VideoClipModel
import io.snaps.basefeed.domain.VideoFeedInteractor
import io.snaps.basefeed.domain.VideoFeedPageModel
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesources.BottomDialogBarVisibilityHandler
import io.snaps.basesubs.data.SubsRepository
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppDeeplink
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.coreuicompose.uikit.bottomsheetdialog.ActionColor
import io.snaps.coreuicompose.uikit.bottomsheetdialog.ActionData
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

// todo through delegation, find a way to provide viewModelScope
abstract class VideoFeedViewModel(
    bottomDialogBarVisibilityHandler: BottomDialogBarVisibilityHandler,
    private val videoFeedType: VideoFeedType,
    private val action: Action,
    private val videoFeedInteractor: VideoFeedInteractor,
    @Bridged private val videoFeedRepository: VideoFeedRepository,
    @Bridged private val profileRepository: ProfileRepository,
    @Bridged private val commentRepository: CommentRepository,
    @Bridged private val subsRepository: SubsRepository,
    val startPosition: Int = 0,
) : SimpleViewModel(),
    BottomDialogBarVisibilityHandler by bottomDialogBarVisibilityHandler {

    private val _uiState = MutableStateFlow(
        UiState(actions = getActions())
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private var commentsLoadJob: Job? = null
    private var authorLoadJob: Job? = null
    private var isLoaded: Boolean = false // to track load for the first item
    private var isRefreshed: Boolean = true
    private var currentVideo: VideoClipModel? = null
    private var videoFeedPageModel: VideoFeedPageModel? = null // todo remove
    private val videoClipsBeingMarkedAsWatched = hashSetOf<Uuid>()

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
            if (videoFeedType == VideoFeedType.Main && !it.isLoading && it.error == null) {
                videoFeedInteractor.insertAds(it)
            } else {
                it
            }.toVideoFeedUiState(
                shimmerListSize = 1,
                onClipClicked = ::onClipClicked,
                onReloadClicked = ::onReloadClicked,
                onListEndReaching = ::onListEndReaching,
            )
        }.onEach { state ->
            _uiState.update { it.copy(videoFeedUiState = state) }
            if (state.isData) {
                if (startPosition == 0 && !isLoaded) {
                    isLoaded = true
                    onScrolledToPosition(0)
                } else if (!isRefreshed) {
                    isRefreshed = true
                    onScrolledToPosition(0)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun onClipClicked(clip: VideoClipModel) {}

    protected fun onReloadClicked() {
        viewModelScope.launch {
            action.execute {
                isRefreshed = false
                videoFeedRepository.refreshFeed(videoFeedType)
            }
        }
    }

    private fun onListEndReaching() {
        viewModelScope.launch { action.execute { videoFeedRepository.loadNextFeedPage(videoFeedType) } }
    }

    fun onScrolledToPosition(position: Int) {
        val current =
            _uiState.value.videoFeedUiState.items.getOrNull(position) as? VideoClipUiState.Data
        val videoClip = current?.clip ?: return
        currentVideo = videoClip
        loadAuthor(videoClip)
        checkIfSubscribed(videoClip.authorId)
    }

    private fun loadComments(videoId: Uuid) {
        commentsLoadJob?.cancel()
        commentsLoadJob = commentRepository.getCommentsState(videoId).map { commentPageModel ->
            commentPageModel.toCommentsUiState(
                shimmerListSize = 10,
                onCommentClicked = {},
                onAvatarClicked = {
                    it.owner?.let {
                        viewModelScope.launch { _command publish Command.OpenProfileScreen(it.userId) }
                    }
                },
                onReloadClicked = {},
                onListEndReaching = { onCommentListEndReaching(videoId) },
            )
        }.onEach { state ->
            _uiState.update { it.copy(commentsUiState = state) }
        }.launchIn(viewModelScope)
    }

    private fun loadAuthor(videoClipModel: VideoClipModel) {
        authorLoadJob?.cancel()
        if (videoClipModel.author != null) {
            _uiState.update {
                it.copy(
                    authorProfileAvatar = videoClipModel.author.avatar,
                    authorName = videoClipModel.author.name
                )
            }
            return
        }

        _uiState.update { it.copy(authorProfileAvatar = null, authorName = "") }
        authorLoadJob = viewModelScope.launch {
            action.execute {
                profileRepository.getUserInfoById(videoClipModel.authorId)
            }.doOnSuccess { profileModel ->
                if (!isActive) return@doOnSuccess
                _uiState.update {
                    it.copy(
                        authorProfileAvatar = profileModel.avatar,
                        authorName = profileModel.name
                    )
                }
            }
        }
    }

    private fun checkIfSubscribed(authorId: Uuid) {
        _uiState.update { it.copy(isSubscribeButtonVisible = false) }
        if (videoFeedType != VideoFeedType.Main) return
        if (profileRepository.isCurrentUser(authorId)) return
        viewModelScope.launch {
            action.execute {
                subsRepository.isSubscribed(authorId)
            }.doOnSuccess { isSubscribed ->
                _uiState.update {
                    it.copy(
                        isSubscribed = isSubscribed,
                        isSubscribeButtonVisible = true,
                    )
                }
            }
        }
    }

    private fun onCommentListEndReaching(videoId: Uuid) {
        viewModelScope.launch { action.execute { commentRepository.loadNextCommentPage(videoId) } }
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

    fun onVideoClipWatchProgressed(clipModel: VideoClipModel) {
        if (videoClipsBeingMarkedAsWatched.contains(clipModel.id)) return
        if (profileRepository.state.value.dataOrCache?.userId == clipModel.authorId) return

        videoClipsBeingMarkedAsWatched.add(clipModel.id)

        viewModelScope.launch {
            action.execute(needsErrorProcessing = false) {
                videoFeedRepository.markWatched(clipModel.id)
            }.doOnError { _, _ ->
                videoClipsBeingMarkedAsWatched.remove(clipModel.id)
            }
        }
    }

    fun onVideoClipStartedPlaying(
        clipModel: VideoClipModel,
        videoDuration: Float,
        skipDuration: Float
    ) {
        viewModelScope.launch {
            action.execute(needsErrorProcessing = false) {
                videoFeedRepository.markShown(
                    videoId = clipModel.id,
                    videoDuration = videoDuration,
                    duration = skipDuration
                )
            }
        }
    }

    fun onLikeClicked(clipModel: VideoClipModel) {
        viewModelScope.launch {
            likeVideoClip(clipModel)
            action.execute {
                videoFeedRepository.like(clipModel.id)
            }
        }
    }

    fun onDoubleLikeClicked(clipModel: VideoClipModel) {
        if (clipModel.isLiked) return
        onLikeClicked(clipModel)
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
        loadComments(clipModel.id)
        _uiState.update {
            it.copy(bottomDialog = BottomDialog.Comments)
        }
        viewModelScope.launch { _command publish Command.ShowBottomDialog }
    }

    fun onShareClicked(clipModel: VideoClipModel) {
        viewModelScope.launch {
            _command publish Command.ShareVideoClipLink(
                AppDeeplink.generateSharingLink(deeplink = AppDeeplink.VideoClip(id = clipModel.id))
            )
        }
    }

    fun onCommentChanged(newValue: TextFieldValue) {
        if (newValue.text.count { it == '\n' } < 6 && newValue.text.length <= 130) {
            _uiState.update { it.copy(comment = newValue) }
        }
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
                    text = removeEmptyLines(uiState.value.comment.text),
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
                    commentCount = it.commentCount + 1
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
        _uiState.update {
            it.copy(bottomDialog = BottomDialog.MoreActions)
        }
        _command publish Command.ShowBottomDialog
    }

    private fun getActions(): List<ActionData> = listOfNotNull(
        ActionData(
            text = StringKey.VideoClipActionDelete.textValue(),
            icon = AppTheme.specificIcons.delete,
            color = ActionColor.Negative,
            onClick = { onDeleteClicked() },
        ).takeIf { videoFeedType is VideoFeedType.User && videoFeedType.userId == null },
    )

    private fun onDeleteClicked() = viewModelScope.launch {
        _command publish Command.HideBottomDialog
        _uiState.update {
            it.copy(dialog = Dialog.ConfirmDeleteVideo)
        }
    }

    fun onDeleteConfirmed() {
        val video = currentVideo ?: return
        viewModelScope.launch {
            action.execute {
                videoFeedRepository.delete(video.id)
            }.doOnSuccess {
                _uiState.update {
                    it.copy(dialog = null)
                }
                videoFeedRepository.refreshFeed(videoFeedType).doOnSuccess {
                    // todo it's not the right place to check for emptiness
                    if (uiState.value.videoFeedUiState.items.isEmpty()) {
                        _command publish Command.CloseScreen
                    }
                }
            }
        }
    }

    fun onDeleteDismissed() {
        _uiState.update {
            it.copy(dialog = null)
        }
    }

    private fun removeEmptyLines(text: String): String {
        return text.split("\n").filter { it.trim().isNotEmpty() }.joinToString("\n")
    }

    fun onSubscribeClicked() {
        val video = currentVideo ?: return
        viewModelScope.launch {
            val isSubscribedInitially = _uiState.value.isSubscribed
            _uiState.update {
                it.copy(isSubscribed = !isSubscribedInitially)
            }
            action.execute {
                if (isSubscribedInitially) {
                    subsRepository.unsubscribe(video.authorId)
                } else {
                    subsRepository.subscribe(video.authorId)
                }.doOnError { _, _ ->
                    _uiState.update {
                        it.copy(isSubscribed = isSubscribedInitially)
                    }
                }.flatMap {
                    profileRepository.updateData(isSilently = true)
                }
            }
        }
    }

    data class UiState(
        val isMuted: Boolean = false,
        val profileAvatar: ImageValue? = null,
        val authorProfileAvatar: ImageValue? = null,
        val authorName: String = "",
        val comment: TextFieldValue = TextFieldValue(""),
        val videoFeedUiState: VideoFeedUiState = VideoFeedUiState(),
        val commentsUiState: CommentsUiState = CommentsUiState(),
        val bottomDialog: BottomDialog = BottomDialog.Comments,
        val dialog: Dialog? = null,
        val actions: List<ActionData>,
        val isSubscribeButtonVisible: Boolean = false,
        val isSubscribed: Boolean = false,
    ) {

        val isCommentSendEnabled get() = comment.text.isNotBlank()
    }

    sealed class Dialog {
        object ConfirmDeleteVideo : Dialog()
    }

    sealed class BottomDialog {
        object Comments : BottomDialog()
        object MoreActions : BottomDialog()
    }

    sealed class Command {
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
        object ShowCommentInputBottomDialog : Command()
        object HideCommentInputBottomDialog : Command()
        object CloseScreen : Command()
        data class ScrollToPosition(val position: Int) : Command()
        data class OpenProfileScreen(val userId: Uuid) : Command()
        data class ShareVideoClipLink(val link: FullUrl) : Command()
    }
}