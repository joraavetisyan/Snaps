package io.snaps.baseprofile.data

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.coredata.database.TokenStorage
import io.snaps.coreui.viewmodel.tryPublish
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface UserSessionTracker {

    val state: StateFlow<State>

    fun init(lifecycle: Lifecycle)

    fun onLogin()

    fun onLogout()

    sealed class State {

        object NotActive : State() // Not registered/authorized

        object Active : State() // Registered/authorized
    }
}

@Singleton
class UserSessionTrackerImpl @Inject constructor(
    tokenStorage: TokenStorage,
) : UserSessionTracker, DefaultLifecycleObserver {

    private val _state = MutableStateFlow<UserSessionTracker.State>(
        UserSessionTracker.State.NotActive
    )
    override val state = _state.asStateFlow()

    init {
        if (tokenStorage.authToken != null) {
            _state tryPublish UserSessionTracker.State.Active
        }
    }

    override fun onLogin() {
        _state tryPublish UserSessionTracker.State.Active
    }

    override fun onLogout() {
        _state tryPublish UserSessionTracker.State.NotActive
    }

    override fun init(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface UserSessionTrackerModule {

    @Binds
    @Singleton
    fun userSessionTracker(tracker: UserSessionTrackerImpl): UserSessionTracker
}