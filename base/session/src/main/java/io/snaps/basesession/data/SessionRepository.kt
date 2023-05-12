package io.snaps.basesession.data

import com.google.firebase.auth.FirebaseAuth
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesources.DeviceInfoProvider
import io.snaps.basewallet.data.WalletRepository
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.OnboardingType
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.database.LogOutReason
import io.snaps.coredata.database.TokenStorage
import io.snaps.coredata.database.UserDataStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface SessionRepository {

    fun isOnboardingShown(type: OnboardingType): Boolean

    suspend fun refresh(): Effect<Completable>

    suspend fun checkStatus(): Effect<Completable>

    suspend fun onLogin(): Effect<Completable>

    suspend fun onWalletConnect(): Effect<Completable>

    fun onInitialize()

    fun onLogout()

    fun forceLogout(reason: LogOutReason)
}

class SessionRepositoryImpl @Inject constructor(
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    private val userSessionTracker: UserSessionTracker,
    private val tokenStorage: TokenStorage,
    private val userDataStorage: UserDataStorage,
    private val deviceInfoProvider: DeviceInfoProvider,
    private val auth: FirebaseAuth,
    private val profileRepository: ProfileRepository,
    private val walletRepository: WalletRepository,
) : SessionRepository {

    override fun isOnboardingShown(type: OnboardingType): Boolean {
        return userDataStorage.isOnboardingShown(type).also {
            userDataStorage.setIsOnboardingShown(type, true)
        }
    }

    override suspend fun checkStatus(): Effect<Completable> {
        val userId: String? = auth.currentUser?.uid
        if (tokenStorage.authToken != null && userId != null) {
            return checkWallet(userId)
        }
        userSessionTracker.onLogin(UserSessionTracker.State.NotActive)
        return Effect.completable
    }

    /**
     * Check if user has wallet connected
     */
    private suspend fun checkWallet(userId: String): Effect<Completable> {
        if (!walletRepository.hasAccount(userId)) {
            return profileRepository.updateData().flatMap {
                userSessionTracker.onLogin(
                    if (it.wallet == null) {
                        UserSessionTracker.State.Active.NeedsWalletConnect
                    } else {
                        UserSessionTracker.State.Active.NeedsWalletImport
                    }
                )
                Effect.completable
            }
        }
        return checkUser().doOnSuccess { ready ->
            if (ready) {
                userSessionTracker.onLogin(UserSessionTracker.State.Active.Ready)
            }
        }.doOnError { _, _ ->
            // todo should we really open Main here?
            userSessionTracker.onLogin(UserSessionTracker.State.Active.Ready)
        }.toCompletable()
    }

    /**
     * Check if user has name and avatar
     */
    private suspend fun checkUser() = profileRepository.updateData().flatMap {
        if (it.name.isBlank() || it.avatarUrl == null) {
            userSessionTracker.onLogin(UserSessionTracker.State.Active.NeedsInitialization)
            Effect.success(false)
        } else {
            Effect.success(true)
        }
    }

    override suspend fun refresh(): Effect<Completable> {
        val token = auth.currentUser?.getIdToken(false)?.await()?.token
        if (token != null) {
            tokenStorage.authToken = token
        } else {
            onLogout()
        }
        return Effect.completable
    }

    override suspend fun onLogin(): Effect<Completable> {
        auth.currentUser?.uid?.let {
            if (walletRepository.hasAccount(it)) {
                walletRepository.setAccountActive(it)
            }
            return checkWallet(it)
        }
        return Effect.completable
    }

    override suspend fun onWalletConnect(): Effect<Completable> {
        return checkUser().doOnSuccess { ready ->
            if (ready) {
                userSessionTracker.onLogin(UserSessionTracker.State.Active.Ready)
            }
        }.toCompletable()
    }

    override fun onInitialize() {
        userSessionTracker.onLogin(UserSessionTracker.State.Active.Ready)
    }

    override fun onLogout() {
        clearData(null)
    }

    override fun forceLogout(reason: LogOutReason) {
        clearData(reason)
    }

    private fun clearData(reason: LogOutReason?) {
        auth.currentUser?.uid?.let {
            walletRepository.deleteAccount(it)
        }
        auth.signOut()
        scope.launch {
            deviceInfoProvider.resetPushToken()
            tokenStorage.reset()
            userDataStorage.reset(reason)
            userSessionTracker.onLogout()
        }
    }
}