package io.snaps.featureprofile.presentation

import io.snaps.basenotifications.domain.NotificationModel
import io.snaps.basenotifications.domain.NotificationPageModel
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.uikit.listtile.EmptyListTileState
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState

sealed interface NotificationUiState {

    val key: Any

    data class Data(
        override val key: Any,
        val item: NotificationModel,
        val onClicked: () -> Unit,
        val onSubscribeClicked: () -> Unit,
    ) : NotificationUiState

    data class Shimmer(override val key: Any) : NotificationUiState

    data class Progress(override val key: Any = -1000) : NotificationUiState
}

data class NotificationsUiState(
    val items: List<NotificationUiState> = emptyList(),
    val errorState: MessageBannerState? = null,
    val emptyState: EmptyListTileState? = null,
    val onListEndReaching: (() -> Unit)? = null,
) {

    val count get() = items.count { it is NotificationUiState.Data }
}

fun NotificationPageModel.toNotificationsUiState(
    shimmerListSize: Int,
    onItemClicked: (NotificationModel) -> Unit,
    onSubscribeClicked: (NotificationModel) -> Unit,
    onReloadClicked: () -> Unit,
    onEmptyClicked: () -> Unit,
    onListEndReaching: () -> Unit,
): NotificationsUiState {
    return when {
        isLoading && loadedPageItems.isEmpty() -> NotificationsUiState(
            items = List(shimmerListSize) {
                NotificationUiState.Shimmer("${NotificationUiState.Shimmer::class.simpleName}$it")
            }
        )
        error != null -> NotificationsUiState(
            errorState = MessageBannerState.defaultState(onReloadClicked)
        )
        loadedPageItems.isEmpty() -> NotificationsUiState(
            emptyState = EmptyListTileState(
                title = StringKey.NotificationsTitleEmpty.textValue(),
                buttonData = EmptyListTileState.ButtonData(
                    text = StringKey.NotificationsActionStartSubscribe.textValue(),
                    onClick = onEmptyClicked,
                )
            ),
        )
        else -> NotificationsUiState(
            items = loadedPageItems.map {
                NotificationUiState.Data(
                    key = it.id,
                    item = it,
                    onClicked = { onItemClicked(it) },
                    onSubscribeClicked = { onSubscribeClicked(it) }
                )
            }.run {
                if (nextPage == null) this
                else this.plus(NotificationUiState.Progress())
            },
            onListEndReaching = onListEndReaching,
        )
    }
}