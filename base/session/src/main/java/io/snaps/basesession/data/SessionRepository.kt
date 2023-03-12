package io.snaps.basesession.data

import io.snaps.baseprofile.data.UserSessionTracker
import io.snaps.basesession.data.model.LogoutRequestDto
import io.snaps.basesources.DeviceInfoProvider
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.crypto.PinCodeWrapper
import io.snaps.coredata.database.LogOutReason
import io.snaps.coredata.database.TokenStorage
import io.snaps.coredata.database.UserDataStorage
import io.snaps.coredata.network.apiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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
    private val logoutApi: LogoutApi,
    private val refreshApi: RefreshApi,
    private val pinCodeWrapper: PinCodeWrapper,
    private val deviceInfoProvider: DeviceInfoProvider,
) : SessionRepository {

    /*
    * обновление токенов при авторизации.
    * используется refresh токен, полученный при авторизации (через ввод pin кода или биометрию)
    * */
    override suspend fun login(): Effect<Completable> {
        onLogin()
        return Effect.completable
    }

    /*
    * обновление токенов без участия пользователя.
    * используется refresh токен из оперативной памяти
    * */
    override suspend fun refresh(): Effect<Completable> {
        return login()
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