package io.snaps.featuretasks.domain

import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basewallet.data.WalletRepository
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.FullUrl
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
                    instagramId = it.id,
                    instagramUsername = it.username,
                    name = user.name,
                    walletAddress = walletRepository.getActiveWalletsReceiveAddresses().first(),
                    avatar = user.avatar?.value as FullUrl,
                )
            } ?: Effect.error(AppError.Unknown())
        }
    }

    override suspend fun disconnectInstagram(): Effect<Completable> {
        return profileRepository.state.value.dataOrCache?.let {
             profileRepository.disconnectInstagram(
                 name = it.name,
                 walletAddress = walletRepository.getActiveWalletsReceiveAddresses().first(),
                 avatar = it.avatar?.value as FullUrl,
            )
        } ?: Effect.error(AppError.Unknown())
    }
}