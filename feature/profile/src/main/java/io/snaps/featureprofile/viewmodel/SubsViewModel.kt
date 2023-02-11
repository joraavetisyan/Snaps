package io.snaps.featureprofile.viewmodel

import androidx.lifecycle.SavedStateHandle
import io.snaps.corecommon.container.ImageValue
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.featureprofile.domain.Sub
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.corecommon.model.SubsPage
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SubsViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val action: Action,
) : SimpleViewModel() {

    private val args = stateHandle.requireArgs<AppRoute.Subs.Args>()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        _uiState.update {
            it.copy(
                nickname = args.nickname,
                totalSubscribers = args.totalSubscribers,
                totalSubscriptions = args.totalSubscriptions,
                initialPage = when (args.subsPage) {
                    SubsPage.Subscriptions -> 0
                    SubsPage.Subscribers -> 1
                },
            )
        }
    }
    data class UiState(
        val initialPage: Int = 0,
        val totalSubscriptions: String = "",
        val totalSubscribers: String = "",
        val nickname: String = "",
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