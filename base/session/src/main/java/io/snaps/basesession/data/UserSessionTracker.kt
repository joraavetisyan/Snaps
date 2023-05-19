package io.snaps.basesession.data

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.corecommon.ext.log
import io.snaps.coreui.viewmodel.tryPublish
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface UserSessionTracker {

    val state: StateFlow<State>

    fun init(lifecycle: Lifecycle)

    fun onLogin(state: State)

    fun onLogout()

    sealed class State {

        object Idle : State() // Init state

        object NotActive : State() // Not registered/authorized

        // Registered/authorized
        sealed class Active : State() {
            object NeedsWalletConnect : Active() // Registered/authorized, but not connected to wallet
            object NeedsWalletImport : Active() // Registered/authorized, connected to wallet previously, needs to import it
            object NeedsInitialization : Active() // Registered/authorized, connected to wallet, but not initialized
            object Ready : Active() // Registered/authorized, connected to wallet, initialized
            object Error : Active()
        }
    }
}

@Singleton
class UserSessionTrackerImpl @Inject constructor() : UserSessionTracker, DefaultLifecycleObserver {

    private val _state = MutableStateFlow<UserSessionTracker.State>(UserSessionTracker.State.Idle)
    override val state = _state.asStateFlow()

    override fun onLogin(state: UserSessionTracker.State) {
        logState(state)
        _state tryPublish state
    }

    private fun logState(state: UserSessionTracker.State) {
        log("UserSessionTrackerState: $state")
    }

    override fun onLogout() {
        logState(UserSessionTracker.State.NotActive)
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