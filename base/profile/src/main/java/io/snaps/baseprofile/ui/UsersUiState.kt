package io.snaps.baseprofile.ui

import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.coreuicompose.uikit.listtile.EmptyListTileState
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState

sealed interface UserUiState {

    val key: Any

    data class Data(
        override val key: Any,
        val user: UserInfoModel,
        val onClicked: () -> Unit,
    ) : UserUiState

    data class Shimmer(override val key: Any) : UserUiState
}

data class UsersUiState(
    val items: List<UserUiState> = emptyList(),
    val errorState: MessageBannerState? = null,
    val emptyState: EmptyListTileState? = null,
    val onListEndReaching: (() -> Unit)? = null,
) {

    val isData get() = items.any { it is UserUiState.Data }
}