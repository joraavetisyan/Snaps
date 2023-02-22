package io.snaps.featurecreate.viewmodel

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.viewmodel.SimpleViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class PreviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : SimpleViewModel() {

    private val args = savedStateHandle.requireArgs<AppRoute.PreviewVideo.Args>()

    private val _uiState = MutableStateFlow(UiState(args.uri))
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    data class UiState(
        val uri: String,
    )

    sealed class Command
}