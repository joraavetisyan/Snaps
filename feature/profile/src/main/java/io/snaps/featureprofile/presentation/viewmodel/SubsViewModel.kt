package io.snaps.featureprofile.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesubs.data.SubsRepository
import io.snaps.basesubs.domain.SubModel
import io.snaps.basesubs.domain.SubPageModel
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.di.Bridged
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.viewmodel.publish
import io.snaps.featureprofile.presentation.screen.ConfirmUnsubscribeData
import io.snaps.featureprofile.presentation.screen.SubsUiState
import io.snaps.featureprofile.presentation.screen.toSubsUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
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
class SubsViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val action: Action,
    @Bridged private val subsRepository: SubsRepository,
    @Bridged private val profileRepository: ProfileRepository,
) : SimpleViewModel() {

    private val args = stateHandle.requireArgs<AppRoute.Subs.Args>()

    private val _uiState = MutableStateFlow(
        UiState(
            nickname = args.userName,
            initialPage = args.subsType.ordinal,
            totalSubscriptions = args.totalSubscriptions,
            totalSubscribers = args.totalSubscribers,
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private var subscribersPageModel: SubPageModel? = null
    private var subscriptionsPageModel: SubPageModel? = null

    init {
        subscribeOnSubscriptions()
        subscribeOnSubscribers()
    }

    private fun subscribeOnSubscribers() {
        subsRepository.getSubscribersState(args.userId).map {
            subscribersPageModel = it
            it.toSubsUiState(
                shimmerListSize = 5,
                onItemClicked = ::onItemClicked,
                onReloadClicked = ::onSubscribersReloadClicked,
                onListEndReaching = ::onSubscriberListEndReaching,
                onSubscribeClicked = ::onSubscribeClicked,
            )
        }.onEach { state ->
            _uiState.update { it.copy(subscribersUiState = state) }
        }.launchIn(viewModelScope)
    }

    private fun subscribeOnSubscriptions() {
        subsRepository.getSubscriptionsState(args.userId).map {
            subscriptionsPageModel = it
            it.toSubsUiState(
                shimmerListSize = 5,
                onItemClicked = ::onItemClicked,
                onReloadClicked = ::onSubscriptionsReloadClicked,
                onListEndReaching = ::onSubscriptionListEndReaching,
                onSubscribeClicked = ::onSubscribeClicked,
            )
        }.onEach { state ->
            _uiState.update { it.copy(subscriptionsUiState = state) }
        }.launchIn(viewModelScope)
    }

    private fun onItemClicked(item: SubModel) = viewModelScope.launch {
        _command publish Command.OpenProfileScreen(userId = item.userId)
    }

    private fun onSubscribeClicked(item: SubModel) = viewModelScope.launch {
        if (item.isSubscribed!!) {
            _uiState.update {
                it.copy(
                    dialog = Dialog.ConfirmUnsubscribe(
                        ConfirmUnsubscribeData(userId = item.userId, avatar = item.avatar, name = item.name)
                    )
                )
            }
        } else {
            _uiState.update { it.copy(isLoading = true) }
            action.execute {
                subsRepository.subscribe(item.userId)
            }.doOnSuccess {
                _uiState.update { it.copy(totalSubscriptions = it.totalSubscriptions + 1) }
                profileRepository.updateData(isSilently = true)
                // todo refresh only subed
                subsRepository.refreshSubscriptions(args.userId)
            }.doOnComplete {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun onSubscribersReloadClicked() = viewModelScope.launch {
        action.execute {
            subsRepository.refreshSubscribers(args.userId)
        }
    }

    private fun onSubscriptionsReloadClicked() = viewModelScope.launch {
        action.execute {
            subsRepository.refreshSubscriptions(args.userId)
        }
    }

    private fun onSubscriptionListEndReaching() = viewModelScope.launch {
        action.execute {
            subsRepository.loadNextSubscriptionsPage(args.userId)
        }
    }

    private fun onSubscriberListEndReaching() = viewModelScope.launch {
        action.execute {
            subsRepository.loadNextSubscribersPage(args.userId)
        }
    }

    fun onUnsubscribeClicked(userId: Uuid) = viewModelScope.launch {
        _uiState.update { it.copy(dialog = null, isLoading = true) }
        action.execute {
            subsRepository.unsubscribe(userId)
        }.doOnSuccess {
            _uiState.update {
                it.copy(
                    totalSubscriptions = if (it.totalSubscriptions > 0) {
                        it.totalSubscriptions - 1
                    } else 0
                )
            }
            profileRepository.updateData(isSilently = true)
            // todo refresh only subed
            subsRepository.refreshSubscriptions(args.userId)
        }.doOnComplete {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onDismissRequest() = viewModelScope.launch {
        _uiState.update { it.copy(dialog = null) }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val initialPage: Int = 0,
        val totalSubscriptions: Int = 0,
        val totalSubscribers: Int = 0,
        val nickname: String = "",
        val subscribersUiState: SubsUiState = SubsUiState(),
        val subscriptionsUiState: SubsUiState = SubsUiState(),
        val dialog: Dialog? = null,
    )

    sealed class Dialog {
        data class ConfirmUnsubscribe(val data: ConfirmUnsubscribeData) : Dialog()
    }

    sealed class Command {
        data class OpenProfileScreen(val userId: Uuid) : Command()
    }
}

// todo test and use on subsequent Effect methods
fun CoroutineScope.async(vararg blocks: suspend () -> Effect<*>) {
    val asyncs = blocks.map { async { it() } }
    launch { asyncs.forEach { it.await() } }
}