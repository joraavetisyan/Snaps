package io.snaps.featureprofile.viewmodel

import io.snaps.corecommon.container.ImageValue
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.featureprofile.domain.Sub
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SubsViewModel @Inject constructor(
    private val action: Action,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    data class UiState(
        val subscriptions: List<Sub> = List(20) {
            Sub(
                image = ImageValue.Url("https://picsum.photos/35"),
                name = "@Subscription$it",
                isSubscribed = true,
            )
        },
        val subscribers: List<Sub> = List(20) {
            Sub(
                image = ImageValue.Url("https://picsum.photos/35"),
                name = "@Subscriber$it",
                isSubscribed = Random.nextBoolean(),
            )
        },
    )

    sealed class Command
}