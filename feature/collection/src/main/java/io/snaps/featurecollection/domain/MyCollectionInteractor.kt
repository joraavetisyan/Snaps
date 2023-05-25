package io.snaps.featurecollection.domain

import io.snaps.basenft.data.NftRepository
import io.snaps.basenft.domain.NftModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.data.model.PaymentsState
import io.snaps.basewallet.data.WalletRepository
import io.snaps.basewallet.data.blockchain.BlockchainTxRepository
import io.snaps.basewallet.domain.NftMintSummary
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.Token
import io.snaps.corecommon.model.TxHash
import io.snaps.coredata.di.Bridged
import javax.inject.Inject

const val minBnb = 0.0017

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
    @Bridged private val profileRepository: ProfileRepository,
    @Bridged private val nftRepository: NftRepository,
    @Bridged private val blockchainTxRepository: BlockchainTxRepository,
    @Bridged private val walletRepository: WalletRepository,
) : MyCollectionInteractor {

    override suspend fun repair(nftModel: NftModel): Effect<TxHash> {
        return profileRepository.updateData(isSilently = true).flatMap { userInfoModel ->
            if (userInfoModel.paymentsState == PaymentsState.Blockchain) {
                if ((walletRepository.snps.value?.coinValue?.value ?: 0.0) < nftModel.repairCost.value) {
                    Effect.error(AppError.Custom(cause = NoEnoughSnpToRepair))
                } else blockchainTxRepository.getRepairNftSign(repairCost = nftModel.repairCost.value).flatMap { sign ->
                    nftRepository.repairNftBlockchain(nftModel = nftModel, txSign = sign)
                }
            } else {
                nftRepository.repairNft(nftModel = nftModel).map { "" }
            }
        }
    }

    override suspend fun mint(nftType: NftType, purchaseToken: Token?): Effect<Completable> {
        require(
            nftType == NftType.Free || (nftType.storeId != null && purchaseToken != null)
        )
        return if (nftType == NftType.Free) {
            nftRepository.mintNft(NftType.Free).toCompletable()
        } else {
            val bnb = walletRepository.bnb.value?.coinValue?.value
            if (bnb == null || bnb < minBnb) Effect.error(AppError.Custom(cause = NoEnoughBnbToMint))
            else {
                blockchainTxRepository.getNftMintSummary(nftType = nftType, amount = 0.0)
                    .flatMap {
                        blockchainTxRepository.getMintNftSign(nftType = nftType, summary = it)
                    }.flatMap {
                        nftRepository.mintNftStore(
                            productId = nftType.storeId!!,
                            purchaseToken = purchaseToken!!,
                            txSign = it,
                        )
                    }
            }
        }
    }

    override suspend fun getNftMintSummary(nftType: NftType): Effect<NftMintSummary> {
        val amount = 0.005
        if ((walletRepository.bnb.value?.coinValue?.value ?: 0.0) < amount) {
            return Effect.error(AppError.Custom(cause = NoEnoughBnbToMint))
        }
        return blockchainTxRepository.getNftMintSummary(nftType = nftType, amount = amount)
    }

    override suspend fun mintOnBlockchain(nftType: NftType, summary: NftMintSummary): Effect<TxHash> {
        return blockchainTxRepository.getMintNftSign(nftType = nftType, summary = summary).flatMap {
            nftRepository.mintNft(type = nftType, txSign = it)
        }
    }
}