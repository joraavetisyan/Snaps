package io.snaps.basesources

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

// todo better way, temp workaround
@Singleton
class BottomBarVisibilitySource @Inject constructor() {

    private val _state = MutableStateFlow(true)
    val state = _state.asStateFlow()

    fun updateState(state: Boolean) {
        _state.update { state }
    }
}