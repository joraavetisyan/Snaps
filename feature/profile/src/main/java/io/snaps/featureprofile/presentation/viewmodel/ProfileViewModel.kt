package io.snaps.featureprofile.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.domain.VideoFeedType
import io.snaps.basefeed.ui.VideoFeedUiState
import io.snaps.basefeed.ui.toVideoFeedUiState
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesubs.data.SubsRepository
import io.snaps.corecommon.model.SubsType
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppDeeplink
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.featureprofile.presentation.screen.ConfirmUnsubscribeData
import io.snaps.featureprofile.presentation.screen.UserInfoTileState
import io.snaps.featureprofile.presentation.toUserInfoTileState
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
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @Bridged private val profileRepository: ProfileRepository,
    @Bridged private val videoFeedRepository: VideoFeedRepository,
    @Bridged private val subsRepository: SubsRepository,
    private val action: Action,
) : SimpleViewModel() {

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
                }.doOnSuccess {
                    if (!profileRepository.isCurrentUser(userId)) {
                        _uiState.update {
                            it.copy(
                                userType = UserType.Other,
                                shareLink = AppDeeplink.generateSharingLink(
                                    deeplink = AppDeeplink.Profile(id = userId),
                                )
                            )
                        }
                        loadUserById(userId)
                        checkIfSubscribed()
                    } else {
                        subscribeOnCurrentUser()
                        loadCurrentUser()
                    }
                }
            }
        } else {
            subscribeOnCurrentUser()
            loadCurrentUser()
        }

        subscribeOnFeed()
        subscribeOnUserLikedFeed()
    }

    private fun subscribeOnCurrentUser() {
        profileRepository.state.onEach { state ->
            _uiState.update {
                it.copy(
                    userInfoTileState = state.toUserInfoTileState(
                        isCurrentUser = true,
                        onSubscribersClick = { onSubscribersClicked(SubsType.Subscribers) },
                        onSubscriptionsClick = { onSubscribersClicked(SubsType.Subscriptions) },
                    ),
                    name = state.dataOrCache?.name.orEmpty(),
                    shareLink = state.dataOrCache?.userId?.let { userId ->
                        AppDeeplink.generateSharingLink(AppDeeplink.Profile(userId))
                    },
                    userType = UserType.Current,
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun loadCurrentUser() = viewModelScope.launch {
        action.execute {
            profileRepository.updateData()
        }
    }

    private fun checkIfSubscribed() = viewModelScope.launch {
        action.execute {
            subsRepository.isSubscribed(args.userId!!)
        }.doOnSuccess { isSubscribed ->
            _uiState.update { it.copy(isSubscribed = isSubscribed) }
        }
    }

    private fun loadUserById(userId: Uuid) = viewModelScope.launch {
        action.execute {
            profileRepository.getUserInfoById(userId)
        }.doOnSuccess { user ->
            _uiState.update {
                it.copy(
                    userInfoTileState = user.toUserInfoTileState(
                        isCurrentUser = false,
                        onSubscribersClick = { onSubscribersClicked(SubsType.Subscribers) },
                        onSubscriptionsClick = { onSubscribersClicked(SubsType.Subscriptions) }
                    ),
                    name = user.name,
                )
            }
        }
    }

    private fun onSubscribersClicked(subsType: SubsType) = viewModelScope.launch {
        val userInfo = uiState.value.userInfoTileState
        if (userInfo is UserInfoTileState.Data) {
            _command publish Command.OpenSubsScreen(
                args = AppRoute.Subs.Args(
                    userId = args.userId,
                    subsType = subsType,
                    userName = uiState.value.name,
                )
            )
        }
    }

    private fun subscribeOnFeed() {
        videoFeedRepository.getFeedState(VideoFeedType.User(args.userId)).map {
            it.toVideoFeedUiState(
                shimmerListSize = 12,
                onClipClicked = {},
                onReloadClicked = ::onFeedReloadClicked,
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

    private fun onFeedReloadClicked() = viewModelScope.launch {
        action.execute {
            videoFeedRepository.refreshFeed(VideoFeedType.User(args.userId))
        }
    }

    private fun subscribeOnUserLikedFeed() {
        videoFeedRepository.getFeedState(VideoFeedType.Liked(args.userId)).map {
            it.toVideoFeedUiState(
                shimmerListSize = 12,
                onClipClicked = {},
                onReloadClicked = ::onUserLikedFeedReloadClicked,
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

    private fun onUserLikedFeedReloadClicked() = viewModelScope.launch {
        action.execute {
            videoFeedRepository.refreshFeed(VideoFeedType.Liked(args.userId))
        }
    }

    fun onSettingsClicked() = viewModelScope.launch {
        _command publish Command.OpenSettingsScreen
    }

    fun onSubscribeClicked() = viewModelScope.launch {
        if (uiState.value.isSubscribed) {
            val userInfo = uiState.value.userInfoTileState
            if (userInfo is UserInfoTileState.Data) {
                _uiState.update {
                    it.copy(
                        dialog = Dialog.ConfirmUnsubscribe(
                            ConfirmUnsubscribeData(
                                userId = requireNotNull(args.userId),
                                avatar = userInfo.profileImage,
                                name = it.name,
                            )
                        )
                    )
                }
            }
        } else {
            _uiState.update {
                it.copy(isSubscribed = !it.isSubscribed)
            }
            action.execute {
                subsRepository.subscribe(requireNotNull(args.userId))
            }.doOnSuccess {
                profileRepository.updateData(isSilently = true)
            }
        }
    }

    fun onUnsubscribeClicked(userId: Uuid) = viewModelScope.launch {
        _uiState.update {
            it.copy(
                dialog = null,
                isSubscribed = !it.isSubscribed,
            )
        }
        action.execute {
            subsRepository.unsubscribe(userId)
        }.doOnSuccess {
            profileRepository.updateData(isSilently = true)
        }
    }

    fun onDismissRequest() = viewModelScope.launch {
        _uiState.update {
            it.copy(dialog = null)
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

    fun onGalleryIconClicked() {
        _uiState.update {
            it.copy(selectedItemIndex = 0)
        }
    }

    fun onLikeIconClicked() {
        _uiState.update {
            it.copy(selectedItemIndex = 1)
        }
    }

    data class UiState(
        val isLoading: Boolean = true,
        val userInfoTileState: UserInfoTileState = UserInfoTileState.Shimmer,
        val name: String = "",
        // if current authed user is subscribed to this user
        val isSubscribed: Boolean = false,
        val userType: UserType = UserType.None,
        val videoFeedUiState: VideoFeedUiState = VideoFeedUiState(),
        val userLikedVideoFeedUiState: VideoFeedUiState = VideoFeedUiState(),
        val dialog: Dialog? = null,
        val shareLink: String? = null,
        val selectedItemIndex: Int = 0,
    )

    sealed class Command {
        object OpenSettingsScreen : Command()
        data class OpenSubsScreen(val args: AppRoute.Subs.Args) : Command()
        data class OpenUserFeedScreen(val userId: Uuid?, val position: Int) : Command()
        data class OpenLikedFeedScreen(val userId: Uuid?, val position: Int) : Command()
    }

    sealed class Dialog {
        data class ConfirmUnsubscribe(val data: ConfirmUnsubscribeData) : Dialog()
    }

    enum class UserType {
        None,
        Current,
        Other,
    }
}