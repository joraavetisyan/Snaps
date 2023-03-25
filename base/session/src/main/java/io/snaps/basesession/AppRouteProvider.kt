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

    val appRouteState: StateFlow<String>

    val menuRouteState: StateFlow<String>

    fun updateAppRouteState(state: String)

    fun updateMenuRouteState(state: String)
}

class AppRouteProviderImpl @Inject constructor() : AppRouteProvider {

    private val _appRouteState = MutableStateFlow(AppRoute.MainBottomBar.path())
    override val appRouteState = _appRouteState.asStateFlow()

    private val _menuRouteState = MutableStateFlow(AppRoute.MainBottomBar.MainTab1Start.path())
    override val menuRouteState = _menuRouteState.asStateFlow()

    override fun updateAppRouteState(state: String) {
        _appRouteState.tryEmit(state)
    }

    override fun updateMenuRouteState(state: String) {
        _menuRouteState.tryEmit(state)
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface AppRouteProviderModule {

    @Binds
    @Singleton
    fun activeAppRouteProvider(provider: AppRouteProviderImpl): AppRouteProvider
}