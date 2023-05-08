package io.snaps.featuretasks.domain

import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basewallet.data.WalletRepository
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.featuretasks.data.InstagramService
import javax.inject.Inject

interface ConnectInstagramInteractor {

    suspend fun connectInstagram(authCode: String): Effect<Completable>

    suspend fun disconnectInstagram(): Effect<Completable>
}

class ConnectInstagramInteractorImpl @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val walletRepository: WalletRepository,
    private val instagramService: InstagramService,
) : ConnectInstagramInteractor {

    override suspend fun connectInstagram(authCode: String): Effect<Completable> {
        return instagramService.getAccessToken(authCode).flatMap {
            instagramService.getUserInfo(it)
        }.flatMap {
            profileRepository.state.value.dataOrCache?.let { user ->
                profileRepository.connectInstagram(
                    instagramUsername = it.username,
                    name = user.name,
                    walletAddress = walletRepository.requireActiveWalletReceiveAddress(),
                    avatar = user.avatarUrl,
                )
            } ?: Effect.error(AppError.Unknown())
        }
    }

    override suspend fun disconnectInstagram(): Effect<Completable> {
        return profileRepository.state.value.dataOrCache?.let {
             profileRepository.disconnectInstagram(
                 name = it.name,
                 walletAddress = walletRepository.requireActiveWalletReceiveAddress(),
                 avatar = it.avatarUrl,
            )
        } ?: Effect.error(AppError.Unknown())
    }
}