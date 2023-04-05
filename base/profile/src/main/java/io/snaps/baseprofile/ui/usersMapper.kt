package io.snaps.baseprofile.ui

import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.baseprofile.domain.UsersPageModel
import io.snaps.corecommon.container.textValue
import io.snaps.coreuicompose.uikit.listtile.EmptyListTileState
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState

sealed interface UserUiState {

    val id: Any

    data class Data(
        override val id: Any,
        val user: UserInfoModel,
        val onClicked: () -> Unit,
    ) : UserUiState

    data class Shimmer(override val id: Any) : UserUiState
}

data class UsersUiState(
    val items: List<UserUiState> = emptyList(),
    val errorState: MessageBannerState? = null,
    val emptyState: EmptyListTileState? = null,
    val onListEndReaching: (() -> Unit)? = null,
) {

    val isData get() = items.any { it is UserUiState.Data }
}

fun UsersPageModel.toUsersUiState(
    shimmerListSize: Int,
    onUserClicked: (UserInfoModel) -> Unit,
    onReloadClicked: () -> Unit,
    onListEndReaching: () -> Unit,
): UsersUiState {
    return when {
        isLoading && loadedPageItems.isEmpty() -> UsersUiState(
            items = List(shimmerListSize) {
                UserUiState.Shimmer("${UserUiState.Shimmer::class.simpleName}$it")
            }
        )
        error != null -> UsersUiState(
            errorState = MessageBannerState.defaultState(onReloadClicked)
        )
        loadedPageItems.isEmpty() -> UsersUiState(
            emptyState = EmptyListTileState(
                title = "No data".textValue(),
            )
        )
        else -> UsersUiState(
            items = loadedPageItems.map {
                UserUiState.Data(
                    id = it.userId,
                    user = it,
                    onClicked = { onUserClicked(it) },
                )
            },
            onListEndReaching = onListEndReaching,
        )
    }
}