package io.snaps.basesession.data

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.coreui.viewmodel.tryPublish
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface UserSessionTracker {

    val state: StateFlow<State>

    fun init(lifecycle: Lifecycle)

    fun onLogin(state: State.Active)

    fun onLogout()

    sealed class State {

        object NotActive : State() // Not registered/authorized

        // Registered/authorized
        sealed class Active : State() {
            object Checking : Active() // Checking user status
            object NeedsWalletConnect : Active() // Registered/authorized, but not connected to wallet
            object NeedsInitialization : Active() // Registered/authorized, connected to wallet, but not initialized
            object NeedsRanking : Active() // Registered/authorized, connected to wallet, initialized, but not ranked
            object Ready : Active() // Registered/authorized, connected to wallet, initialized, ranked
        }
    }
}

@Singleton
class UserSessionTrackerImpl @Inject constructor() : UserSessionTracker, DefaultLifecycleObserver {

    private val _state = MutableStateFlow<UserSessionTracker.State>(
        UserSessionTracker.State.NotActive
    )
    override val state = _state.asStateFlow()

    override fun onLogin(state: UserSessionTracker.State.Active) {
        _state tryPublish state
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