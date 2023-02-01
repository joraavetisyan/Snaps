package io.snaps.featuremain.presentation.viewmodel

import io.snaps.basesources.LocationSource
import io.snaps.corecommon.container.ImageValue
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.featuremain.data.StubApi
import io.snaps.featuremain.domain.Nft
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class ItemListViewModel @Inject constructor(
    private val source: LocationSource,
    private val api: StubApi,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    data class UiState(
        val isLoading: Boolean = false,
        val items: List<Nft> = listOf(
            Nft(ImageValue.Url("https://picsum.photos/200"), "0.51\$", "6%", "60%"),
            Nft(ImageValue.Url("https://picsum.photos/200"), "0.51\$", "6%", "60%"),
            Nft(ImageValue.Url("https://picsum.photos/200"), "0.51\$", "6%", "60%"),
        ),
    )

    sealed class Command
}