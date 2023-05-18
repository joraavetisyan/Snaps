package io.snaps.featurecollection.domain

import io.snaps.basenft.data.NftRepository
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.data.model.PaymentsState
import io.snaps.basewallet.data.blockchain.BlockchainTxRepository
import io.snaps.basewallet.domain.NftMintSummary
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.NftModel
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.Token
import io.snaps.corecommon.model.TxHash
import javax.inject.Inject

interface MyCollectionInteractor {

    suspend fun repair(nftModel: NftModel): Effect<TxHash>

    suspend fun mint(
        nftType: NftType,
        purchaseToken: Token? = null,
    ): Effect<Completable>

    suspend fun getNftMintSummary(nftType: NftType): Effect<NftMintSummary>

    suspend fun mintOnBlockchain(nftType: NftType, summary: NftMintSummary): Effect<TxHash>
}

class MyCollectionInteractorImpl @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val nftRepository: NftRepository,
    private val blockchainTxRepository: BlockchainTxRepository,
) : MyCollectionInteractor {

    override suspend fun repair(nftModel: NftModel): Effect<TxHash> {
        return profileRepository.updateData(isSilently = true).flatMap { userInfoModel ->
            if (userInfoModel.paymentsState == PaymentsState.Blockchain) {
                blockchainTxRepository.repairNft(nftModel = nftModel).flatMap { hash ->
                    nftRepository.repairNft(nftModel = nftModel, transactionHash = hash).map { hash }
                }
            } else {
                nftRepository.repairNft(
                    nftModel = nftModel,
                    offChainAmount = nftModel.repairCost.toLong(),
                ).map { "" }
            }
        }
    }

    override suspend fun mint(nftType: NftType, purchaseToken: Token?): Effect<Completable> {
        require(
            nftType == NftType.Free || (nftType.storeId != null && purchaseToken != null)
        )
        return if (nftType == NftType.Free) {
            nftRepository.mintNft(NftType.Free)
        } else {
            nftRepository.mintNftStore(nftType.storeId!!, purchaseToken!!)
        }
    }

    override suspend fun getNftMintSummary(nftType: NftType): Effect<NftMintSummary> {
        return blockchainTxRepository.getNftMintSummary(nftType)
    }

    override suspend fun mintOnBlockchain(
        nftType: NftType,
        summary: NftMintSummary,
    ): Effect<TxHash> = blockchainTxRepository.mintNft(nftType = nftType, summary = summary).flatMap { hash ->
        nftRepository.mintNft(type = nftType, transactionHash = hash)
            .flatMap { nftRepository.saveProcessingNft(nftType) }
            .flatMap { nftRepository.updateNftCollection() }
            .map { hash }
    }
}