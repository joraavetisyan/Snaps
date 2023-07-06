package io.snaps.featurequests.domain

import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basewallet.data.WalletRepository
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.di.Bridged
import io.snaps.featurequests.data.InstagramService
import javax.inject.Inject

interface ConnectInstagramInteractor {

    suspend fun connectInstagramWithAuthCode(authCode: String): Effect<Completable>

    suspend fun connectInstagramWithUsername(username: String): Effect<Completable>

    suspend fun disconnectInstagram(): Effect<Completable>
}

class ConnectInstagramInteractorImpl @Inject constructor(
    @Bridged private val profileRepository: ProfileRepository,
    @Bridged private val walletRepository: WalletRepository,
    private val instagramService: InstagramService,
) : ConnectInstagramInteractor {

    override suspend fun connectInstagramWithAuthCode(authCode: String): Effect<Completable> {
        return instagramService.getAccessToken(authCode).flatMap {
            instagramService.getUserInfo(it)
        }.flatMap {
            profileRepository.state.value.dataOrCache?.let { user ->
                profileRepository.connectInstagram(
                    instagramUsername = it.username,
                    name = user.name,
                    address = walletRepository.requireActiveWalletReceiveAddress(),
                    avatar = user.avatarUrl,
                )
            } ?: Effect.error(AppError.Unknown())
        }
    }

    override suspend fun connectInstagramWithUsername(username: String): Effect<Completable> {
        return profileRepository.state.value.dataOrCache?.let { user ->
            profileRepository.connectInstagram(
                instagramUsername = username,
                name = user.name,
                address = walletRepository.requireActiveWalletReceiveAddress(),
                avatar = user.avatarUrl,
            )
        } ?: Effect.error(AppError.Unknown())
    }

    override suspend fun disconnectInstagram(): Effect<Completable> {
        return profileRepository.state.value.dataOrCache?.let {
             profileRepository.disconnectInstagram(
                 name = it.name,
                 address = walletRepository.requireActiveWalletReceiveAddress(),
                 avatar = it.avatarUrl,
            )
        } ?: Effect.error(AppError.Unknown())
    }
}