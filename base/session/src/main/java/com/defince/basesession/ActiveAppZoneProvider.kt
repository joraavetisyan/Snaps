package com.defince.basesession

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/*
* отслеживает зону приложения, в которой находится пользователь в данный момент
* */
interface ActiveAppZoneProvider {

    val state: StateFlow<State>

    fun updateState(state: State)

    sealed class State {
        object Registration : State()
        object Authorized : State()
    }
}

class ActiveAppZoneProviderImpl @Inject constructor() : ActiveAppZoneProvider {

    private val _state = MutableStateFlow<ActiveAppZoneProvider.State>(ActiveAppZoneProvider.State.Registration)
    override val state = _state.asStateFlow()

    override fun updateState(state: ActiveAppZoneProvider.State) {
        _state.update { state }
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface ActiveAppZoneProviderModule {

    @Binds
    @Singleton
    fun activeAppZoneProvider(provider: ActiveAppZoneProviderImpl): ActiveAppZoneProvider
}