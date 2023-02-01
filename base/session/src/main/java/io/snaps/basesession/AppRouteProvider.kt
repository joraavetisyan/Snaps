package io.snaps.basesession

import io.snaps.corenavigation.AppRoute
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface AppRouteProvider {

    val state: StateFlow<String>

    fun updateState(state: String)
}

class AppRouteProviderImpl @Inject constructor() : AppRouteProvider {

    private val _state = MutableStateFlow(AppRoute.MainBottomBar.path())
    override val state = _state.asStateFlow()

    override fun updateState(state: String) {
        _state.tryEmit(state)
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface AppRouteProviderModule {

    @Binds
    @Singleton
    fun activeAppRouteProvider(provider: AppRouteProviderImpl): AppRouteProvider
}