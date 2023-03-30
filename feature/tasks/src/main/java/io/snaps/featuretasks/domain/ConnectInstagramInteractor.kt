package io.snaps.featuretasks.domain

import io.snaps.baseprofile.data.ProfileRepository
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
    private val instagramService: InstagramService,
) : ConnectInstagramInteractor {

    override suspend fun connectInstagram(authCode: String): Effect<Completable> {
        return instagramService.getAccessToken(authCode).flatMap {
            instagramService.getUserInfo(it)
        }.flatMap {
            profileRepository.connectInstagram(
                instagramUserId = it.id,
                username = it.username,
            )
        }
    }

    override suspend fun disconnectInstagram(): Effect<Completable> {
        return profileRepository.disconnectInstagram()
    }
}