package io.snaps.featurecollection.domain

import io.snaps.basenft.data.NftRepository
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.data.model.PaymentsState
import io.snaps.basewallet.data.WalletRepository
import io.snaps.basewallet.domain.NftMintSummary
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.NftModel
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.Token
import javax.inject.Inject

interface MyCollectionInteractor {

    suspend fun repair(nftModel: NftModel): Effect<Completable>

    /**
     * returns: Mint transaction hash
     */
    suspend fun mintOnBlockchain(nftType: NftType, summary: NftMintSummary): Effect<Token>
}

class MyCollectionInteractorImpl @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val nftRepository: NftRepository,
    private val walletRepository: WalletRepository,
) : MyCollectionInteractor {

    override suspend fun repair(nftModel: NftModel): Effect<Completable> {
        return profileRepository.updateData(isSilently = true).flatMap { userInfoModel ->
            if (userInfoModel.paymentsState == PaymentsState.Blockchain) {
                walletRepository.repairNft(nftModel = nftModel).flatMap {
                    nftRepository.repairNft(nftModel = nftModel, data = it)
                }
            } else {
                nftRepository.repairNft(
                    nftModel = nftModel,
                    offChainAmount = nftModel.repairCost.toLong(),
                )
            }
        }
    }

    override suspend fun mintOnBlockchain(
        nftType: NftType,
        summary: NftMintSummary,
    ): Effect<Token> {
        return walletRepository.mintNft(nftType = nftType, summary = summary).flatMap { hash ->
            nftRepository.mintNft(
                type = nftType,
                data = hash,
                walletAddress = walletRepository.getActiveWalletReceiveAddress()!!, // todo
            ).flatMap {
                nftRepository.saveProcessingNft(nftType)
            }.map {
                hash
            }
        }
    }
}