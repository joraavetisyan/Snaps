package io.snaps.basesession.data

import com.google.firebase.auth.FirebaseAuth
import io.snaps.baseprofile.data.UserSessionTracker
import io.snaps.basesources.DeviceInfoProvider
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.database.LogOutReason
import io.snaps.coredata.database.TokenStorage
import io.snaps.coredata.database.UserDataStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface SessionRepository {

    suspend fun login(): Effect<Completable>

    suspend fun refresh(): Effect<Completable>

    fun onLogin()

    fun logout()

    fun forceLogout(reason: LogOutReason)
}

class SessionRepositoryImpl @Inject constructor(
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val userSessionTracker: UserSessionTracker,
    private val tokenStorage: TokenStorage,
    private val userDataStorage: UserDataStorage,
    private val deviceInfoProvider: DeviceInfoProvider,
    private val auth: FirebaseAuth,
) : SessionRepository {

    override suspend fun login(): Effect<Completable> {
        onLogin()
        return Effect.completable
    }

    override suspend fun refresh(): Effect<Completable> {
        log("Refreshing token")
        val token = auth.currentUser?.getIdToken(false)?.await()?.token
        if (token != null) {
            tokenStorage.authToken = token
            onLogin()
        } else {
            logout()
        }
        log("Refresh result: $token")
        return Effect.completable
    }

    override fun onLogin() {
        userSessionTracker.onLogin()
    }

    override fun logout() {
        clearData(null)
    }

    override fun forceLogout(reason: LogOutReason) {
        clearData(reason)
    }

    private fun clearData(reason: LogOutReason?) {
        scope.launch {
            /*apiCall(ioDispatcher) {
                logoutApi.logout(LogoutRequestDto(deviceInfoProvider.getDeviceId()))
            }*/
            deviceInfoProvider.resetPushToken()
            tokenStorage.reset()
            userDataStorage.reset(reason)
            userSessionTracker.onLogout()
            // todo clear wallets
        }
    }
}