package io.snaps.featuretasks.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.corecommon.model.FullUrl
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.featuretasks.data.InstagramService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectInstagramViewModel @Inject constructor(
    private val instagramService: InstagramService,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(
        UiState(
            url = instagramService.getAuthRequestUrl(),
            redirectUrl = instagramService.getRedirectUrl(),
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onAuthCodeReceived(url: FullUrl) = viewModelScope.launch {
        instagramService.getAuthCode(url)?.let {
            _command publish Command.CloseScreen(it)
        }
    }

    data class UiState(
        val url: FullUrl,
        val redirectUrl: FullUrl,
    )

    sealed class Command {
        data class CloseScreen(val authCode: String) : Command()
    }
}