package io.snaps.featurebottombar.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basesources.BottomBarVisibilitySource
import io.snaps.coreui.viewmodel.SimpleViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BottomBarViewModel @Inject constructor(
    bottomBarVisibilitySource: BottomBarVisibilitySource,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        bottomBarVisibilitySource.state.onEach { isBottomBarVisible ->
            _uiState.update { it.copy(isBottomBarVisible = isBottomBarVisible) }
        }.launchIn(viewModelScope)
    }

    data class UiState(
        val isBottomBarVisible: Boolean = true,
    )

    sealed interface Command
}