package io.snaps.basesession.data

import com.google.firebase.auth.FirebaseAuth
import dagger.Lazy
import io.snaps.baseprofile.data.ProfileApi
import io.snaps.basesources.DeviceInfoProvider
import io.snaps.basewallet.data.WalletDataManager
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.OnboardingType
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.database.LogOutReason
import io.snaps.coredata.database.TokenStorage
import io.snaps.coredata.database.UserDataStorage
import io.snaps.coredata.di.UserSessionComponentManager
import io.snaps.coredata.network.apiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.net.HttpURLConnection
import javax.inject.Inject

interface SessionRepository {

    fun isOnboardingShown(type: OnboardingType): Boolean

    suspend fun tryStatusCheck(): Effect<Completable>

    suspend fun tryLogin(): Effect<Completable>

    suspend fun tryRefresh(): Effect<Completable>

    suspend fun onWalletConnected(): Effect<Completable>

    fun onInitialized(usedTags: Boolean)

    fun onSelectedInterests()

    fun logout()

    fun forceLogout(reason: LogOutReason)
}

class SessionRepositoryImpl @Inject constructor(
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val userSessionComponentManager: UserSessionComponentManager,
    private val userSessionTracker: UserSessionTracker,
    private val tokenStorage: TokenStorage,
    private val userDataStorage: UserDataStorage,
    private val deviceInfoProvider: DeviceInfoProvider,
    private val auth: FirebaseAuth,
    private val profileApi: Lazy<ProfileApi>,
    private val walletDataManager: WalletDataManager,
) : SessionRepository {

    override fun isOnboardingShown(type: OnboardingType): Boolean {
        return userDataStorage.isOnboardingShown(type).also {
            userDataStorage.setIsOnboardingShown(type, true)
        }
    }

    override suspend fun tryStatusCheck(): Effect<Completable> {
        val userId = auth.currentUser?.uid
        if (tokenStorage.authToken != null && userId != null) {
            return checkStatus(userId)
        }

        logout()
        return Effect.error(AppError.Unknown())
    }

    private suspend fun checkStatus(userId: Uuid): Effect<Completable> {
        suspend fun check() = apiCall(ioDispatcher) {
            profileApi.get().userInfo()
        }.doOnSuccess { user ->
            when {
                // todo check for account AND wallets
                walletDataManager.hasAccount(userId) -> if (user.name.isNullOrBlank() || user.avatarUrl == null) {
                    userSessionTracker.onLogin(UserSessionTracker.State.Active.NeedsInitialization)
                } else {
                    checkUsedTags(user.isUsedTags ?: true)
                }
                else -> userSessionTracker.onLogin(
                    if (user.wallet == null) UserSessionTracker.State.Active.NeedsWalletConnect
                    else UserSessionTracker.State.Active.NeedsWalletImport
                )
            }
        }

        var check = check()

        if (check.errorOrNull?.code == HttpURLConnection.HTTP_UNAUTHORIZED && tryRefresh().isSuccess) {
            check = check()
        }

        if (check.isError) {
            userSessionTracker.onLogin(UserSessionTracker.State.Active.Error)
        }

        return check.toCompletable()
    }

    private fun checkUsedTags(usedTags: Boolean) {
        if (usedTags) {
            userSessionTracker.onLogin(UserSessionTracker.State.Active.Ready)
        } else {
            userSessionTracker.onLogin(UserSessionTracker.State.Active.NeedsInterestsSelection)
        }
    }

    override suspend fun tryLogin(): Effect<Completable> {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            walletDataManager.clear(userId)
            return checkStatus(userId)
        }

        logout()
        return Effect.error(AppError.Unknown())
    }

    override suspend fun tryRefresh(): Effect<Completable> {
        val token = auth.currentUser?.getIdToken(false)?.await()?.token
        if (token != null) {
            tokenStorage.authToken = token
            return Effect.completable
        }

        logout()
        return Effect.error(AppError.Unknown())
    }

    override suspend fun onWalletConnected(): Effect<Completable> {
        return tryStatusCheck()
    }

    override fun onInitialized(usedTags: Boolean) {
        checkUsedTags(usedTags)
    }

    override fun onSelectedInterests() {
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
            walletDataManager.clear(it)
        }
        auth.signOut()
        scope.launch {
            deviceInfoProvider.resetPushToken()
            tokenStorage.reset()
            userDataStorage.reset(reason)
            userSessionTracker.onLogout()
            userSessionComponentManager.onUserLoggedOut()
        }
    }
}