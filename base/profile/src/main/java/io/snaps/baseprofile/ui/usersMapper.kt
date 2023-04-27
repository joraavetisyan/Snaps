package io.snaps.baseprofile.ui

import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.baseprofile.domain.UsersPageModel
import io.snaps.corecommon.container.textValue
import io.snaps.coreuicompose.uikit.listtile.EmptyListTileState
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState

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
                title = "No data".textValue(), // todo localize
            )
        )
        else -> UsersUiState(
            items = loadedPageItems.map {
                UserUiState.Data(
                    key = it.entityId,
                    user = it,
                    onClicked = { onUserClicked(it) },
                )
            },
            onListEndReaching = onListEndReaching,
        )
    }
}