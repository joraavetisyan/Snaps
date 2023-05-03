package io.snaps.featurecollection.domain

import io.snaps.basenft.data.NftRepository
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.data.model.PaymentsState
import io.snaps.basewallet.data.WalletRepository
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.NftModel
import javax.inject.Inject

interface MyCollectionInteractor {

    suspend fun repairGlasses(nftModel: NftModel): Effect<Completable>
}

class MyCollectionInteractorImpl @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val nftRepository: NftRepository,
    private val walletRepository: WalletRepository,
) : MyCollectionInteractor {

    override suspend fun repairGlasses(nftModel: NftModel): Effect<Completable> {
        return profileRepository.updateData(isSilently = true).flatMap { userInfoModel ->
            if (userInfoModel.paymentsState == PaymentsState.Blockchain) {
                walletRepository.repairNftOnBlockchain(nftModel = nftModel).flatMap {
                    nftRepository.repairNft(nftModel = nftModel, repairTxHash = it)
                }
            } else {
                nftRepository.repairNft(
                    nftModel = nftModel,
                    offChainAmount = nftModel.repairCost.toLong(),
                )
            }
        }
    }
}