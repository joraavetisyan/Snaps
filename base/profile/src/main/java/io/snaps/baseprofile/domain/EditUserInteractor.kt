package io.snaps.baseprofile.domain

import io.snaps.basefile.data.FileRepository
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basewallet.data.WalletRepository
import io.snaps.corecommon.model.BuildInfo
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.FullUrl
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.ApiService
import java.io.File
import javax.inject.Inject

interface EditUserInteractor {

    suspend fun editUser(avatarFile: File? = null, userName: String? = null): Effect<Completable>
}

class EditUserInteractorImpl @Inject constructor(
    @Bridged private val profileRepository: ProfileRepository,
    private val fileRepository: FileRepository,
    @Bridged private val walletRepository: WalletRepository,
    private val buildInfo: BuildInfo,
) : EditUserInteractor {

    override suspend fun editUser(avatarFile: File?, userName: String?): Effect<Completable> {
        return avatarFile?.let { file ->
            fileRepository.uploadFile(file).flatMap {
                edit(
                    avatarUrl = "${ApiService.General.getBaseUrl(buildInfo)}v1/file?fileId=${it.id}",
                    userName = userName,
                )
            }
        } ?: edit(
            userName = userName,
            avatarUrl = null,
        )
    }

    private suspend fun edit(avatarUrl: FullUrl?, userName: String?): Effect<Completable> {
        return profileRepository.state.value.dataOrCache?.let {
            profileRepository.editUser(
                avatar = avatarUrl ?: it.avatarUrl,
                userName = userName ?: it.name,
                address = walletRepository.requireActiveWalletReceiveAddress(),
            )
        } ?: Effect.completable
    }
}