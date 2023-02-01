package io.snaps.baseprofile.data

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.snaps.corecommon.model.BuildInfo
import io.snaps.coredata.database.TokenStorage
import io.snaps.coredata.database.UserDataStorage
import io.snaps.coreui.viewmodel.tryPublish
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

private val SESSION_EXPIRED_TIME = 5.minutes

/*
* - отслеживает статус сессии пользователя
* - отслеживает статус сессии приложения и необходимость обновления при долгой неактивности пользователя
* */
interface UserSessionTracker {

    val state: StateFlow<State>

    fun init(lifecycle: Lifecycle)

    fun onLogin()

    fun onLogout()

    sealed class State {

        object NotActive : State()

        data class Active(val actualState: ActualState) : State()

        val isRefreshed get() = this is Active && actualState == ActualState.Refreshed
        val isNeedRefresh get() = this is Active && actualState == ActualState.NeedRefresh
    }

    enum class ActualState {
        NotStarted, Refreshed, NeedRefresh
    }
}

@Singleton
class UserSessionTrackerImpl @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val userDataStorage: UserDataStorage,
    private val buildInfo: BuildInfo,
) : UserSessionTracker, DefaultLifecycleObserver {

    private val _state =
        MutableStateFlow<UserSessionTracker.State>(UserSessionTracker.State.NotActive)
    override val state = _state.asStateFlow()

    private var pauseTime: Duration? = null

    init {
        // fixme сбросить сессию при неактивности пользователя более 90 дней
        if (userDataStorage.isRegistrationFinished) {
            _state tryPublish UserSessionTracker.State.Active(UserSessionTracker.ActualState.NotStarted)
        }
    }

    override fun init(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }

    override fun onLogin() {
        _state tryPublish UserSessionTracker.State.Active(UserSessionTracker.ActualState.Refreshed)
    }

    override fun onLogout() {
        _state tryPublish UserSessionTracker.State.NotActive
    }

    override fun onResume(owner: LifecycleOwner) {
        val pauseTime = pauseTime
        this.pauseTime = null
        _state.update {
            when {
                it.isRefreshed -> UserSessionTracker.State.Active(
                    needRefresh(UserSessionTracker.ActualState.Refreshed, pauseTime)
                )
                else -> it
            }
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        pauseTime = System.currentTimeMillis().milliseconds
    }

    private fun needRefresh(
        state: UserSessionTracker.ActualState,
        pauseTime: Duration?,
    ): UserSessionTracker.ActualState {
        val resumeTime = System.currentTimeMillis().milliseconds

        return when {
            pauseTime != null && resumeTime - pauseTime > SESSION_EXPIRED_TIME -> UserSessionTracker.ActualState.NeedRefresh
            else -> state
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface UserSessionTrackerModule {

    @Binds
    @Singleton
    fun userSessionTracker(tracker: UserSessionTrackerImpl): UserSessionTracker
}