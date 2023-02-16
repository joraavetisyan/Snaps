package io.snaps.featureprofile.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.ui.VideoFeedUiState
import io.snaps.basefeed.ui.toVideoFeedUiState
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.model.SubsType
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.getArg
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.featureprofile.data.SubsRepository
import io.snaps.featureprofile.domain.Sub
import io.snaps.featureprofile.presentation.toUserInfoTileState
import io.snaps.featureprofile.presentation.screen.UserInfoTileState
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
    private val profileRepository: ProfileRepository,
    private val videoFeedRepository: VideoFeedRepository,
    private val subsRepository: SubsRepository,
    private val action: Action,
) : SimpleViewModel() {

    private val args = savedStateHandle.getArg<AppRoute.Profile.Args>()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        if (args?.userId != null) {
            _uiState.update {
                it.copy(userType = UserType.Other)
            }
            loadUserById(requireNotNull(args.userId))
        } else {
            subscribeOnCurrentUser()
            loadCurrentUser()
        }
        subscribeOnFeed()
    }

    private fun subscribeOnCurrentUser() {
        profileRepository.state.onEach { state ->
            _uiState.update {
                it.copy(
                    userInfoTileState = state.toUserInfoTileState(
                        onSubscribersClick = { onSubscribersClicked(SubsType.Subscribers) },
                        onSubscriptionsClick = { onSubscribersClicked(SubsType.Subscriptions) }
                    ),
                    nickname = state.dataOrCache?.name.orEmpty(),
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun loadCurrentUser() = viewModelScope.launch {
        action.execute {
            profileRepository.updateData()
        }
    }

    private fun loadUserById(userId: Uuid) = viewModelScope.launch {
        action.execute {
            profileRepository.getUserInfoById(userId)
        }.doOnSuccess { user ->
            _uiState.update {
                it.copy(
                    userInfoTileState = user.toUserInfoTileState(
                        onSubscribersClick = { onSubscribersClicked(SubsType.Subscribers) },
                        onSubscriptionsClick = { onSubscribersClicked(SubsType.Subscriptions) }
                    ),
                    nickname = user.name,
                )
            }
        }
    }

    private fun onSubscribersClicked(subsPage: SubsType) = viewModelScope.launch {
        val userInfo = uiState.value.userInfoTileState
        if (userInfo is UserInfoTileState.Data) {
            _command publish Command.OpenSubsScreen(
                args = AppRoute.Subs.Args(
                    subsPage = subsPage,
                    nickname = uiState.value.nickname,
                    totalSubscriptions = userInfo.subscriptions,
                    totalSubscribers = userInfo.subscribers,
                )
            )
        }
    }

    private fun subscribeOnFeed() {
        videoFeedRepository.getUserFeedState(args?.userId).map {
            it.toVideoFeedUiState(
                shimmerListSize = 12,
                onClipClicked = {},
                onReloadClicked = {},
                onListEndReaching = ::onListEndReaching,
            )
        }.onEach { state ->
            _uiState.update { it.copy(videoFeedUiState = state) }
        }.launchIn(viewModelScope)
    }

    private fun onListEndReaching() {
        viewModelScope.launch {
            action.execute {
                videoFeedRepository.loadNextUserFeedPage(args?.userId)
            }
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
                        dialog = SubsViewModel.Dialog.ConfirmUnsubscribe(
                            Sub(
                                userId = requireNotNull(args?.userId),
                                image = userInfo.profileImage,
                                name = it.nickname,
                                isSubscribed = it.isSubscribed,
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
                subsRepository.subscribe(requireNotNull(args?.userId))
            }
        }
    }

    fun onUnsubscribeClicked(item: Sub) = viewModelScope.launch {
        _uiState.update {
            it.copy(
                dialog = null,
                isSubscribed = !it.isSubscribed,
            )
        }
        action.execute {
            subsRepository.unsubscribe(item.userId)
        }
    }

    fun onDismissRequest() = viewModelScope.launch {
        _uiState.update {
            it.copy(dialog = null)
        }
    }

    data class UiState(
        val isLoading: Boolean = true,
        val userInfoTileState: UserInfoTileState = UserInfoTileState.Shimmer,
        val nickname: String = "",
        val isSubscribed: Boolean = false,
        val userType: UserType = UserType.Current,
        val videoFeedUiState: VideoFeedUiState = VideoFeedUiState(),
        val dialog: SubsViewModel.Dialog? = null,
    )

    sealed class Command {
        object OpenSettingsScreen : Command()
        data class OpenSubsScreen(val args: AppRoute.Subs.Args) : Command()
    }

    sealed class Dialog {
        data class ConfirmUnsubscribe(val data: Sub) : Dialog()
    }

    enum class UserType {
        Current, Other
    }
}

data class Photo(
    val image: ImageValue,
    val views: String,
)