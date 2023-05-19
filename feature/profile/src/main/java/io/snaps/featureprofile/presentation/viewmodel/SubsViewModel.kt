package io.snaps.featureprofile.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basesubs.data.SubsRepository
import io.snaps.basesubs.domain.SubModel
import io.snaps.basesubs.domain.SubPageModel
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.di.Bridged
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.viewmodel.publish
import io.snaps.featureprofile.presentation.screen.ConfirmUnsubscribeData
import io.snaps.featureprofile.presentation.screen.SubsUiState
import io.snaps.featureprofile.presentation.screen.toSubsUiState
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
) : SimpleViewModel() {

    private val args = stateHandle.requireArgs<AppRoute.Subs.Args>()

    private val _uiState = MutableStateFlow(
        UiState(
            nickname = args.userName,
            totalSubscribers = args.totalSubscribers,
            totalSubscriptions = args.totalSubscriptions,
            initialPage = args.subsType.ordinal,
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
            subscribe(item.userId, true)
            action.execute {
                subsRepository.subscribe(item.userId)
            }
        }
    }

    private fun subscribe(userId: Uuid, isSubscribe: Boolean) {
        val subscribers = subscribersPageModel?.loadedPageItems?.map {
            when (it.userId) {
                userId -> it.copy(isSubscribed = isSubscribe)
                else -> it
            }
        } ?: emptyList()
        val subscriptions = subscriptionsPageModel?.loadedPageItems?.map {
            when (it.userId) {
                userId -> it.copy(isSubscribed = isSubscribe)
                else -> it
            }
        } ?: emptyList()

        subscribersPageModel = subscribersPageModel?.copy(loadedPageItems = subscribers)
        subscriptionsPageModel = subscriptionsPageModel?.copy(loadedPageItems = subscriptions)
        _uiState.update {
            it.copy(
                subscribersUiState = subscribersPageModel.applySubToState(),
                subscriptionsUiState = subscriptionsPageModel.applySubToState(),
            )
        }
    }

    private fun SubPageModel?.applySubToState() = this?.toSubsUiState(
        shimmerListSize = 5,
        onItemClicked = ::onItemClicked,
        onReloadClicked = ::onSubscriptionsReloadClicked,
        onListEndReaching = ::onSubscriptionListEndReaching,
        onSubscribeClicked = ::onSubscribeClicked,
    ) ?: SubsUiState()

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
        _uiState.update {
            it.copy(dialog = null)
        }
        subscribe(userId = userId, isSubscribe = false)
        action.execute {
            subsRepository.unsubscribe(userId)
        }
    }

    fun onDismissRequest() = viewModelScope.launch {
        _uiState.update {
            it.copy(dialog = null)
        }
    }

    data class UiState(
        val initialPage: Int = 0,
        val totalSubscriptions: String = "",
        val totalSubscribers: String = "",
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