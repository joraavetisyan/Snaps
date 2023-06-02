package io.snaps.basesession.data

import com.google.firebase.auth.FirebaseAuth
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesources.DeviceInfoProvider
import io.snaps.basewallet.data.WalletRepository
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.OnboardingType
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.database.LogOutReason
import io.snaps.coredata.database.TokenStorage
import io.snaps.coredata.database.UserDataStorage
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.di.UserSessionComponentManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.net.HttpURLConnection
import javax.inject.Inject

interface SessionRepository {

    fun isOnboardingShown(type: OnboardingType): Boolean

    suspend fun refresh(): Effect<Boolean>

    suspend fun checkStatus(): Effect<Completable>

    suspend fun onLogin(): Effect<Completable>

    suspend fun onWalletConnected(): Effect<Completable>

    fun onInitialized()

    fun logout()

    fun forceLogout(reason: LogOutReason)
}

class SessionRepositoryImpl @Inject constructor(
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    private val userSessionComponentManager: UserSessionComponentManager,
    private val userSessionTracker: UserSessionTracker,
    private val tokenStorage: TokenStorage,
    private val userDataStorage: UserDataStorage,
    private val deviceInfoProvider: DeviceInfoProvider,
    private val auth: FirebaseAuth,
    @Bridged private val profileRepository: ProfileRepository,
    @Bridged private val walletRepository: WalletRepository,
) : SessionRepository {

    override fun isOnboardingShown(type: OnboardingType): Boolean {
        return userDataStorage.isOnboardingShown(type).also {
            userDataStorage.setIsOnboardingShown(type, true)
        }
    }

    override suspend fun checkStatus(): Effect<Completable> {
        if (tokenStorage.authToken == null) {
            auth.signOut()
        }
        val userId: String? = auth.currentUser?.uid
        if (tokenStorage.authToken != null && userId != null) {
            return checkStatus(userId)
        }
        userSessionTracker.onLogin(UserSessionTracker.State.NotActive)
        return Effect.completable
    }

    /**
     * Check if user has wallet connected
     */
    private suspend fun checkStatus(userId: Uuid): Effect<Completable> {
        suspend fun check() = if (!walletRepository.hasAccount(userId)) {
            profileRepository.updateData().flatMap {
                userSessionTracker.onLogin(
                    if (it.wallet == null) {
                        UserSessionTracker.State.Active.NeedsWalletConnect
                    } else {
                        UserSessionTracker.State.Active.NeedsWalletImport
                    }
                )
                Effect.completable
            }
        } else {
            profileRepository.updateData().flatMap {
                Effect.success(!(it.name.isBlank() || it.avatarUrl == null))
            }.doOnSuccess { ready ->
                if (ready) {
                    userSessionTracker.onLogin(UserSessionTracker.State.Active.Ready)
                } else {
                    userSessionTracker.onLogin(UserSessionTracker.State.Active.NeedsInitialization)
                }
            }.toCompletable()
        }

        var check = check()

        if (check.isError) {
            if (check.errorOrNull?.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                if (refresh().data == true) {
                    check = check()
                }
            } else {
                userSessionTracker.onLogin(UserSessionTracker.State.Active.Error)
            }
        }

        return check
    }

    // todo move side-effect of logging out from here
    override suspend fun refresh(): Effect<Boolean> {
        val token = auth.currentUser?.getIdToken(false)?.await()?.token
        return if (token != null) {
            tokenStorage.authToken = token
            Effect.success(true)
        } else {
            logout()
            Effect.success(false)
        }
    }

    override suspend fun onLogin(): Effect<Completable> {
        auth.currentUser?.uid?.let {
            if (walletRepository.hasAccount(it)) {
                walletRepository.clear(it)
            }
            return checkStatus(it)
        }
        return Effect.error(AppError.Unknown())
    }

    override suspend fun onWalletConnected(): Effect<Completable> {
        return checkStatus()
    }

    override fun onInitialized() {
        userSessionTracker.onLogin(UserSessionTracker.State.Active.Ready)
    }

    override fun logout() {
        clearData(null)
    }

    override fun forceLogout(reason: LogOutReason) {
        clearData(reason)
    }

    private fun clearData(reason: LogOutReason?) {
        auth.currentUser?.uid?.let {
            walletRepository.clear(it)
        }
        auth.signOut()
        scope.launch {
            deviceInfoProvider.resetPushToken()
            tokenStorage.reset()
            userDataStorage.reset(reason)
            userSessionTracker.onLogout()
        }
        userSessionComponentManager.onUserLoggedOut()
    }
}