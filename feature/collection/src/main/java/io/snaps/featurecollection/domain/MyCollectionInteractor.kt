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

    suspend fun mint(
        nftType: NftType,
        purchaseToken: Token? = null,
    ): Effect<Completable>

    suspend fun getNftMintSummary(nftType: NftType): Effect<NftMintSummary>

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
                    nftRepository.repairNft(nftModel = nftModel, transactionHash = it)
                }
            } else {
                nftRepository.repairNft(
                    nftModel = nftModel,
                    offChainAmount = nftModel.repairCost.toLong(),
                )
            }
        }
    }

    override suspend fun mint(nftType: NftType, purchaseToken: Token?): Effect<Completable> {
        require(
            nftType != NftType.Free || (nftType.storeId != null && purchaseToken != null)
        )
        return if (nftType == NftType.Free) {
            nftRepository.mintNft(NftType.Free)
        } else {
            nftRepository.mintNftStore(nftType.storeId!!, purchaseToken!!)
        }
    }

    override suspend fun getNftMintSummary(nftType: NftType): Effect<NftMintSummary> {
        return walletRepository.getNftMintSummary(nftType)
    }

    override suspend fun mintOnBlockchain(
        nftType: NftType,
        summary: NftMintSummary,
    ): Effect<Token> {
        return walletRepository.mintNft(nftType = nftType, summary = summary).flatMap { hash ->
            nftRepository.mintNft(
                type = nftType,
                transactionHash = hash
            ).flatMap {
                nftRepository.saveProcessingNft(nftType)
            }.map {
                hash
            }
        }
    }
}