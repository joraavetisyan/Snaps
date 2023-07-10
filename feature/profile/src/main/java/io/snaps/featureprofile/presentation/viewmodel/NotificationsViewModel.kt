package io.snaps.featureprofile.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basenotifications.data.NotificationsRepository
import io.snaps.basenotifications.domain.NotificationModel
import io.snaps.basenotifications.domain.NotificationPageModel
import io.snaps.basesubs.data.SubsRepository
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.di.Bridged
import io.snaps.coreui.viewmodel.publish
import io.snaps.featureprofile.presentation.NotificationsUiState
import io.snaps.featureprofile.presentation.toNotificationsUiState
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
class NotificationsViewModel @Inject constructor(
    private val action: Action,
    @Bridged private val subsRepository: SubsRepository,
    @Bridged private val notificationRepository: NotificationsRepository,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private var notificationPageModel: NotificationPageModel? = null

    init {
        subscribeOnNotifications()
    }

    private fun subscribeOnNotifications() {
        notificationRepository.getNotificationsState().map {
            val notifications = it.loadedPageItems.map { notification ->
                notification.copy(
                    isSubscribed = subsRepository.isSubscribed(notification.actionCreateUserId).data ?: false
                )
            }
            notificationPageModel = it.copy(loadedPageItems = notifications)
            notificationPageModel?.toNotificationsUiState(
                shimmerListSize = 6,
                onItemClicked = ::onItemClicked,
                onReloadClicked = ::onReloadClicked,
                onSubscribeClicked = ::onSubscribeClicked,
                onListEndReaching = ::onListEndReaching,
                onEmptyClicked = ::onEmptyClicked,
            ) ?: NotificationsUiState()
        }.onEach { state ->
            _uiState.update { it.copy(notificationsUiState = state) }
        }.launchIn(viewModelScope)
    }

    private fun onItemClicked(item: NotificationModel) = viewModelScope.launch {
        _command publish Command.OpenProfileScreen(userId = item.actionCreateUserId)
    }

    private fun onReloadClicked() = viewModelScope.launch {
        action.execute {
            notificationRepository.refreshNotifications()
        }
    }

    private fun onEmptyClicked() {
        viewModelScope.launch {
            _command publish Command.OpenMainScreen
        }
    }
    private fun onListEndReaching() = viewModelScope.launch {
        action.execute {
            notificationRepository.loadNextNotificationPage()
        }
    }

    private fun onSubscribeClicked(model: NotificationModel) {
        viewModelScope.launch {
            updateSubscribe(model, !model.isSubscribed)
            action.execute {
                if (model.isSubscribed) {
                    subsRepository.unsubscribe(model.actionCreateUserId)
                } else {
                    subsRepository.subscribe(model.actionCreateUserId)
                }.doOnError { _, _ ->
                    updateSubscribe(model, model.isSubscribed)
                }
            }
        }
    }

    private fun updateSubscribe(model: NotificationModel, isSubscribe: Boolean) {
        val notifications = notificationPageModel?.loadedPageItems?.map { notification ->
            when (notification.id) {
                model.id -> notification.copy(isSubscribed = isSubscribe)
                else -> notification
            }
        } ?: emptyList()
        notificationPageModel = notificationPageModel?.copy(loadedPageItems = notifications)
        applyNotificationToState()
    }

    private fun applyNotificationToState() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    notificationsUiState = notificationPageModel?.toNotificationsUiState(
                        shimmerListSize = 6,
                        onItemClicked = ::onItemClicked,
                        onReloadClicked = ::onReloadClicked,
                        onSubscribeClicked = ::onSubscribeClicked,
                        onListEndReaching = ::onListEndReaching,
                        onEmptyClicked = ::onEmptyClicked,
                    ) ?: NotificationsUiState(),
                )
            }
        }
    }

    data class UiState(
        val notificationsUiState: NotificationsUiState = NotificationsUiState(),
    )

    sealed class Command {
        data class OpenProfileScreen(val userId: Uuid) : Command()
        object OpenMainScreen : Command()
    }
}