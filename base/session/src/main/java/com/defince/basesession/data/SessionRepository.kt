package com.defince.basesession.data

import com.defince.baseprofile.data.UserSessionTracker
import com.defince.basesession.data.model.LogoutRequestDto
import com.defince.basesession.data.model.RefreshRequestDto
import com.defince.basesources.DeviceInfoProvider
import com.defince.corecommon.model.Completable
import com.defince.corecommon.model.Effect
import com.defince.corecommon.model.Token
import com.defince.corecommon.model.generateCurrentDateTime
import com.defince.coredata.coroutine.ApplicationCoroutineScope
import com.defince.coredata.coroutine.IoDispatcher
import com.defince.coredata.crypto.PinCodeWrapper
import com.defince.coredata.database.LogOutReason
import com.defince.coredata.database.TokenStorage
import com.defince.coredata.database.UserDataStorage
import com.defince.coredata.network.apiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import javax.inject.Inject

interface SessionRepository {

    suspend fun login(decodedRefreshToken: Token): Effect<Completable>

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
    override suspend fun login(decodedRefreshToken: Token): Effect<Completable> {
        return apiCall(ioDispatcher) {
            refreshApi.refresh(
                RefreshRequestDto(
                    refreshToken = decodedRefreshToken,
                    deviceId = deviceInfoProvider.getDeviceId(),
                    deviceName = deviceInfoProvider.getDeviceName(),
                    requestDatetime = generateCurrentDateTime(),
                )
            )
        }.doOnSuccess {
            pinCodeWrapper.update(it.accessToken, it.refreshToken)
            onLogin()
        }.doOnError { error, _ ->
            if (error.code == HttpURLConnection.HTTP_BAD_REQUEST) {
                // Refresh token has expired
                logout()
            }
        }.toCompletable()
    }

    /*
    * обновление токенов без участия пользователя.
    * используется refresh токен из оперативной памяти
    * */
    override suspend fun refresh(): Effect<Completable> {
        return login(pinCodeWrapper.getRefreshToken())
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
            if (userSessionTracker.state.value.isRefreshed) {
                apiCall(ioDispatcher) {
                    logoutApi.logout(LogoutRequestDto(deviceInfoProvider.getDeviceId()))
                }
            }
            deviceInfoProvider.resetPushToken()
            tokenStorage.reset()
            userDataStorage.reset(reason)
            userSessionTracker.onLogout()
        }
    }
}