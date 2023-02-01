package io.snaps.basesession

import com.google.firebase.messaging.RemoteMessage
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

interface NotificationInfoProvider {

    val state: SharedFlow<State>

    fun updateState(state: RemoteMessage)

    data class State(
        val data: Map<String, String>,
    )

    enum class Type(val serialName: String) {
        Example("example"),
    }
}

val NotificationInfoProvider.State.type: NotificationInfoProvider.Type?
    get() = data["type"]?.let { type -> NotificationInfoProvider.Type.values().find { it.serialName == type } }

class NotificationInfoProviderImpl @Inject constructor(
    @ApplicationCoroutineScope private val coroutineScope: CoroutineScope,
) : NotificationInfoProvider {

    private val _state = MutableSharedFlow<NotificationInfoProvider.State>()
    override val state = _state.asSharedFlow()

    override fun updateState(state: RemoteMessage) {
        coroutineScope.launch {
            _state.emit(NotificationInfoProvider.State(data = state.data))
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface NotificationInfoProviderModule {

    @Binds
    @Singleton
    fun notificationInfoProvider(provider: NotificationInfoProviderImpl): NotificationInfoProvider
}