package io.snaps.basesources

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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

interface BottomDialogBarVisibilityHandler {

    fun onBottomDialogStateChange(hidden: Boolean)
}

class BottomDialogBarVisibilityHandlerImplDelegate @Inject constructor(
    private val bottomBarVisibilitySource: BottomBarVisibilitySource,
) : BottomDialogBarVisibilityHandler {

    override fun onBottomDialogStateChange(hidden: Boolean) {
        bottomBarVisibilitySource.updateState(hidden)
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface DataBindModule {

    @Binds
    @Singleton
    fun bottomBarVisibilityHandler(bind: BottomDialogBarVisibilityHandlerImplDelegate): BottomDialogBarVisibilityHandler
}