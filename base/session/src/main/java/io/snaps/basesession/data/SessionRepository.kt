package io.snaps.basesession.data

import com.google.firebase.auth.FirebaseAuth
import io.snaps.basenft.data.NftRepository
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesources.DeviceInfoProvider
import io.snaps.basewallet.data.WalletRepository
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.database.LogOutReason
import io.snaps.coredata.database.TokenStorage
import io.snaps.coredata.database.UserDataStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface SessionRepository {

    suspend fun refresh(): Effect<Completable>

    suspend fun checkStatus(): Effect<Completable>

    suspend fun onLogin(): Effect<Completable>

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
    private val nftRepository: NftRepository,
) : SessionRepository {

    init {
        scope.launch { checkStatus() }
    }

    override suspend fun checkStatus(): Effect<Completable> {
        val userId: String? = auth.currentUser?.uid
        if (tokenStorage.authToken != null && userId != null) {
            userSessionTracker.onLogin(UserSessionTracker.State.Active.Checking)
            return checkWallet(userId)
        }
        return Effect.completable
    }

    /**
     * Check if user has wallet connected
     */
    private suspend fun checkWallet(userId: String): Effect<Completable> {
        if (!walletRepository.hasAccount(userId)) {
            userSessionTracker.onLogin(UserSessionTracker.State.Active.NeedsWalletConnect)
            return Effect.completable
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
        if (it.name.isBlank() || it.avatar == null) {
            userSessionTracker.onLogin(UserSessionTracker.State.Active.NeedsInitialization)
            Effect.success(false)
        } else {
            checkNft()
        }
    }

    /**
     * Check if user has NFTs in collection
     */
    private suspend fun checkNft() = nftRepository.updateNftCollection().flatMap {
        if (it.isEmpty()) {
            userSessionTracker.onLogin(UserSessionTracker.State.Active.NeedsRanking)
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
        }
        return checkStatus()
    }

    override fun onLogout() {
        clearData(null)
    }

    override fun forceLogout(reason: LogOutReason) {
        clearData(reason)
    }

    private fun clearData(reason: LogOutReason?) {
        auth.signOut()
        walletRepository.setAccountInactive()
        scope.launch {
            deviceInfoProvider.resetPushToken()
            tokenStorage.reset()
            userDataStorage.reset(reason)
            userSessionTracker.onLogout()
        }
    }
}