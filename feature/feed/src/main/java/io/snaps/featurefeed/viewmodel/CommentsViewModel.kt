package io.snaps.featurefeed.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.corecommon.container.ImageValue
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.featurefeed.domain.Comment
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val action: Action,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    data class UiState(
        val comments: List<Comment> = List(20) {
            Comment(
                id = "current$it",
                ownerImage = ImageValue.Url("https://picsum.photos/32"),
                ownerName = "Owner",
                text = "Mauris sed eget nunc lacus velit amet vel.. Mauris sed eget n",
                likes = 12,
                isLiked = it % 2 == 0,
                time = "2h",
                isOwnerVerified = it % 3 == 0,
                ownerTitle = if (it % 3 == 0) "Title" else null,
            )
        },
    )

    sealed class Command
}