package io.snaps.featureprofile.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.data.UploadStatusSource
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.basefeed.ui.CreateCheckHandler
import io.snaps.basefeed.ui.VideoFeedUiState
import io.snaps.basefeed.ui.toVideoFeedUiState
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesubs.data.SubsRepository
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.SubsType
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppDeeplink
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.coreuicompose.uikit.listtile.EmptyListTileState
import io.snaps.featureprofile.presentation.screen.UserInfoTileState
import io.snaps.featureprofile.presentation.toUserInfoTileState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
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
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    createCheckHandler: CreateCheckHandler,
    private val action: Action,
    private val uploadStatusSource: UploadStatusSource,
    @Bridged private val profileRepository: ProfileRepository,
    @Bridged private val videoFeedRepository: VideoFeedRepository,
    @Bridged private val subsRepository: SubsRepository,
) : SimpleViewModel(), CreateCheckHandler by createCheckHandler {

    private val args = savedStateHandle.requireArgs<AppRoute.Profile.Args>()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        val userId = args.userId
        if (userId != null) {
            viewModelScope.launch {
                action.execute {
                    profileRepository.updateData()
                }.doOnComplete {
                    if (profileRepository.isCurrentUser(userId)) {
                        subscribeOnCurrentUser()
                        subscribeOnFeed()
                        subscribeOnUserLikedFeed()
                    } else {
                        loadUserById(userId)
                        checkIfSubscribed()
                        subscribeOnUserLikedFeed()
                    }
                }
            }
        } else {
            subscribeOnCurrentUser()
            loadCurrentUser()
            subscribeOnFeed()
            subscribeOnUserLikedFeed()
        }
        refreshFeed()
        refreshUserLiked()
    }

    private fun subscribeOnCurrentUser() {
        _uiState.update { it.copy(userType = UserType.Current) }
        profileRepository.state.onEach { state ->
            _uiState.update {
                it.copy(
                    userInfoTileState = state.toUserInfoTileState(
                        onSubscribersClick = { onSubscribersClicked(SubsType.Subscribers) },
                        onSubscriptionsClick = { onSubscribersClicked(SubsType.Subscriptions) },
                        onEditProfileClick = { onEditProfileClicked() },
                        isUserCurrent = _uiState.value.userType == UserType.Current
                    ),
                    name = state.dataOrCache?.name.orEmpty(),
                    userImage = state.dataOrCache?.avatar,
                    shareLink = state.dataOrCache?.userId?.let { userId ->
                        AppDeeplink.generateSharingLink(AppDeeplink.Profile(userId))
                    },
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            action.execute {
                profileRepository.updateData(isSilently = true)
            }
        }
    }

    private fun loadUserById(userId: Uuid) {
        _uiState.update {
            it.copy(
                userType = UserType.Other,
                shareLink = AppDeeplink.generateSharingLink(
                    deeplink = AppDeeplink.Profile(id = userId),
                )
            )
        }
        viewModelScope.launch {
            action.execute {
                profileRepository.getUserInfoById(userId)
            }.doOnSuccess { user ->
                _uiState.update {
                    it.copy(
                        userInfoTileState = user.toUserInfoTileState(
                            onSubscribersClick = { onSubscribersClicked(SubsType.Subscribers) },
                            onSubscriptionsClick = { onSubscribersClicked(SubsType.Subscriptions) },
                            onEditProfileClick = { onEditProfileClicked() },
                            isUserCurrent = _uiState.value.userType == UserType.Current,
                        ),
                        name = user.name,
                        userImage = user.avatar,
                    )
                }
            }.doOnComplete {
                // Subscribing here to have the user name on empty screen
                subscribeOnFeed()
            }
        }
    }

    private fun checkIfSubscribed() {
        viewModelScope.launch {
            action.execute {
                subsRepository.isSubscribed(args.userId!!)
            }.doOnSuccess { isSubscribed ->
                _uiState.update { it.copy(isSubscribed = isSubscribed) }
            }
        }
    }

    private fun onSubscribersClicked(subsType: SubsType) {
        viewModelScope.launch {
            val userInfo = uiState.value.userInfoTileState
            if (userInfo is UserInfoTileState.Data) {
                _command publish Command.OpenSubsScreen(
                    args = AppRoute.Subs.Args(
                        userId = args.userId,
                        subsType = subsType,
                        userName = uiState.value.name,
                        totalSubscribers = userInfo.subscribers,
                        totalSubscriptions = userInfo.subscriptions,
                    )
                )
            }
        }
    }

    private fun subscribeOnFeed() {
        videoFeedRepository.getFeedState(VideoFeedType.User(args.userId)).map {
            it.toVideoFeedUiState(
                shimmerListSize = 12,
                emptyMessage = when (_uiState.value.userType) {
                    UserType.None,
                    UserType.Other -> StringKey.ProfileMessageEmptyVideos.textValue(uiState.value.name)

                    UserType.Current -> StringKey.MessageEmptyVideoFeed.textValue()
                },
                emptyImage = ImageValue.ResVector(R.drawable.ic_add_video),
                emptyButtonData = EmptyListTileState.ButtonData(
                    onClick = ::onCreateVideoClicked,
                    text = StringKey.ProfileActionAddVideo.textValue(),
                ).takeIf { uiState.value.userType == UserType.Current },
                onClipClicked = {},
                onReloadClicked = ::refreshFeed,
                onListEndReaching = ::onListEndReaching,
            )
        }.onEach { state ->
            _uiState.update { it.copy(videoFeedUiState = state) }
        }.launchIn(viewModelScope)
    }

    private fun onListEndReaching() {
        viewModelScope.launch {
            action.execute {
                videoFeedRepository.loadNextFeedPage(VideoFeedType.User(args.userId))
            }
        }
    }

    private fun refreshFeed() {
        viewModelScope.launch {
            action.execute {
                videoFeedRepository.refreshFeed(VideoFeedType.User(args.userId))
            }.doOnComplete {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    private fun subscribeOnUserLikedFeed() {
        videoFeedRepository.getFeedState(VideoFeedType.Liked(args.userId)).map {
            it.toVideoFeedUiState(
                shimmerListSize = 12,
                onClipClicked = {},
                onReloadClicked = ::refreshUserLiked,
                onListEndReaching = ::onUserLikedListEndReaching,
            )
        }.onEach { state ->
            _uiState.update { it.copy(userLikedVideoFeedUiState = state) }
        }.launchIn(viewModelScope)
    }

    private fun onUserLikedListEndReaching() {
        viewModelScope.launch {
            action.execute {
                videoFeedRepository.loadNextFeedPage(VideoFeedType.Liked(args.userId))
            }
        }
    }

    private fun refreshUserLiked() {
        viewModelScope.launch {
            action.execute {
                videoFeedRepository.refreshFeed(VideoFeedType.Liked(args.userId))
            }.doOnComplete {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun onSettingsClicked() {
        viewModelScope.launch {
            _command publish Command.OpenSettingsScreen
        }
    }

    fun onWalletClicked() {
        viewModelScope.launch {
            _command publish Command.OpenWalletScreen
        }
    }

    private fun onEditProfileClicked() {
        viewModelScope.launch {
            _command publish Command.OpenEditProfileScreen
        }
    }

    fun onNotificationsClicked() {
        viewModelScope.launch {
            _command publish Command.OpenNotificationsScreen
        }
    }

    fun onCreateVideoClicked() {
        viewModelScope.launch { tryOpenCreate() }
    }

    fun onSubscribeClicked() = viewModelScope.launch {
        val isSubscribed = uiState.value.isSubscribed
        _uiState.update { it.copy(isSubscribed = !it.isSubscribed) }
        if (isSubscribed) {
            action.execute {
                subsRepository.unsubscribe(requireNotNull(args.userId))
            }.doOnSuccess {
                profileRepository.updateData(isSilently = true)
            }
        } else {
            action.execute {
                subsRepository.subscribe(requireNotNull(args.userId))
            }.doOnSuccess {
                profileRepository.updateData(isSilently = true)
            }
        }
    }

    fun onVideoClipClicked(position: Int) {
        viewModelScope.launch {
            _command publish Command.OpenUserFeedScreen(userId = args.userId, position = position)
        }
    }

    fun onUserLikedVideoClipClicked(position: Int) {
        viewModelScope.launch {
            _command publish Command.OpenLikedFeedScreen(userId = args.userId, position = position)
        }
    }

    fun onTabClicked(selectedItemIndex: Int) {
        _uiState.update { it.copy(selectedItemIndex = selectedItemIndex) }
    }

    fun onRefreshPulled() {
        _uiState.update { it.copy(isRefreshing = true) }
        when (uiState.value.selectedItemIndex) {
            0 -> refreshFeed()
            1 -> refreshUserLiked()
        }
    }

    fun uploadState(videoId: Uuid): Flow<UploadStatusSource.State>? {
        return uploadStatusSource.listenToByVideoId(videoId)
    }

    fun onRetryUploadClicked(videoId: Uuid) {
        viewModelScope.launch {
            action.execute {
                videoFeedRepository.retryUpload(videoId)
            }
        }
    }

    data class UiState(
        val isRefreshing: Boolean = false,
        val isLoading: Boolean = true,
        val userInfoTileState: UserInfoTileState = UserInfoTileState.Shimmer,
        val name: String = "",
        val userImage: ImageValue? = null,
        // if current authed user is subscribed to this user
        val isSubscribed: Boolean = false,
        val userType: UserType = UserType.None,
        val videoFeedUiState: VideoFeedUiState = VideoFeedUiState(),
        val userLikedVideoFeedUiState: VideoFeedUiState = VideoFeedUiState(),
        val shareLink: String? = null,
        val selectedItemIndex: Int = 0,
    )

    sealed class Command {
        object OpenSettingsScreen : Command()
        object OpenNotificationsScreen : Command()
        object OpenWalletScreen : Command()
        object OpenEditProfileScreen : Command()
        object OpenCreateVideoScreen : Command()
        data class OpenSubsScreen(val args: AppRoute.Subs.Args) : Command()
        data class OpenUserFeedScreen(val userId: Uuid?, val position: Int) : Command()
        data class OpenLikedFeedScreen(val userId: Uuid?, val position: Int) : Command()
    }

    enum class UserType {
        None,
        Current,
        Other,
    }
}