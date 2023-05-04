package io.snaps.featureinitialization.domain

import io.snaps.basefile.data.FileRepository
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basewallet.data.WalletRepository
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import java.io.File
import javax.inject.Inject

interface CreateUserInteractor {

    suspend fun createUser(avatarFile: File, userName: String): Effect<Completable>
}

class CreateUserInteractorImpl @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val fileRepository: FileRepository,
    private val walletRepository: WalletRepository,
) : CreateUserInteractor {

    override suspend fun createUser(avatarFile: File, userName: String): Effect<Completable> {
        return fileRepository.uploadFile(avatarFile).flatMap {
            profileRepository.createUser(
                fileId = it.id,
                userName = userName,
                walletAddress = walletRepository.requireActiveWalletReceiveAddress(),
            )
        }
    }
}