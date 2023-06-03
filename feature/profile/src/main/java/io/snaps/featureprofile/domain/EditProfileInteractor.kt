package io.snaps.featureprofile.domain

import io.snaps.basefile.data.FileRepository
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basewallet.data.WalletRepository
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.di.Bridged
import java.io.File
import javax.inject.Inject

interface EditProfileInteractor {

    suspend fun editName(userName: String): Effect<Completable>

    suspend fun editAvatar(avatarFile: File): Effect<Completable>
}

class EditProfileInteractorImpl @Inject constructor(
    @Bridged private val profileRepository: ProfileRepository,
    private val fileRepository: FileRepository,
    @Bridged private val walletRepository: WalletRepository,
) : EditProfileInteractor {

    override suspend fun editName(userName: String): Effect<Completable> {
        return profileRepository.state.value.dataOrCache?.let { user ->
            profileRepository.editProfile(
                avatarUrl = user.avatarUrl,
                userName = userName,
                address = walletRepository.requireActiveWalletReceiveAddress(),
            )
        } ?: Effect.completable
    }

    override suspend fun editAvatar(avatarFile: File): Effect<Completable> {
        return fileRepository.uploadFile(avatarFile).flatMap {
            profileRepository.state.value.dataOrCache?.let { user ->
                profileRepository.createUser(
                    fileId = it.id,
                    userName = user.name,
                    address = walletRepository.requireActiveWalletReceiveAddress(),
                )
            } ?: Effect.completable
        }
    }
}