package io.snaps.featurecollection.domain

import io.snaps.basenft.data.NftRepository
import io.snaps.basenft.data.model.MintMysteryBoxResponseDto
import io.snaps.basenft.domain.NftModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.data.model.PaymentsState
import io.snaps.basewallet.data.WalletRepository
import io.snaps.basewallet.data.blockchain.BlockchainTxRepository
import io.snaps.basewallet.domain.NftMintSummary
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.MysteryBoxType
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.Token
import io.snaps.corecommon.model.TxHash
import io.snaps.coredata.di.Bridged
import javax.inject.Inject

interface MyCollectionInteractor {

    suspend fun repair(nftModel: NftModel): Effect<TxHash>

    suspend fun mint(
        nftType: NftType,
        purchaseToken: Token? = null,
    ): Effect<TxHash>

    suspend fun getNftMintSummary(nftType: NftType, cost: Double): Effect<NftMintSummary>

    suspend fun getMysteryBoxMintSummary(cost: Double): Effect<NftMintSummary>

    suspend fun mintOnBlockchain(nftType: NftType, summary: NftMintSummary): Effect<TxHash>

    suspend fun mysteryBoxMintOnBlockchain(mysteryBoxType: MysteryBoxType, summary: NftMintSummary): Effect<MintMysteryBoxResponseDto>
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
            }.doOnSuccess {
                walletRepository.updateTotalBalance()
            }
        }
    }

    override suspend fun mint(nftType: NftType, purchaseToken: Token?): Effect<TxHash> {
        require(
            nftType == NftType.Free || (nftType.storeId != null && purchaseToken != null)
        )
        return if (nftType == NftType.Free) {
            nftRepository.mintNft(NftType.Free)
        } else {
            val bnb = walletRepository.bnb.value?.coinValue?.value
            if (bnb == null) Effect.error(AppError.Custom(cause = BalanceInSync))
            else {
                blockchainTxRepository.getNftMintSummary(nftType = nftType, amount = 0.0, gasLimit = 170_000L)
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
        }.doOnSuccess {
            profileRepository.updateData(isSilently = true)
            walletRepository.updateTotalBalance()
        }
    }

    override suspend fun getNftMintSummary(nftType: NftType, cost: Double): Effect<NftMintSummary> {
        val balance = walletRepository.bnb.value?.coinValue?.value
        return when {
            balance == null -> Effect.error(AppError.Custom(cause = BalanceInSync))
            balance < cost -> Effect.error(AppError.Custom(cause = NoEnoughBnbToMint))
            else -> blockchainTxRepository.getNftMintSummary(nftType = nftType, amount = cost)
        }
    }

    override suspend fun getMysteryBoxMintSummary(cost: Double): Effect<NftMintSummary> {
        val balance = walletRepository.bnb.value?.coinValue?.value
        return when {
            balance == null -> Effect.error(AppError.Custom(cause = BalanceInSync))
            balance < cost -> Effect.error(AppError.Custom(cause = NoEnoughBnbToMint))
            else -> blockchainTxRepository.getMysteryBoxMintSummary(amount = cost)
        }
    }

    override suspend fun mintOnBlockchain(nftType: NftType, summary: NftMintSummary): Effect<TxHash> {
        return blockchainTxRepository.getMintNftSign(nftType = nftType, summary = summary).flatMap {
            nftRepository.mintNft(type = nftType, txSign = it)
        }.doOnSuccess {
            profileRepository.updateData(isSilently = true)
            walletRepository.updateTotalBalance()
        }
    }

    override suspend fun mysteryBoxMintOnBlockchain(mysteryBoxType: MysteryBoxType, summary: NftMintSummary): Effect<MintMysteryBoxResponseDto> {
        return blockchainTxRepository.getMintMysteryBoxSign(summary = summary).flatMap {
            nftRepository.mintMysteryBox(mysteryBoxType = mysteryBoxType, txSign = it)
        }.doOnSuccess {
            profileRepository.updateData(isSilently = true)
            walletRepository.updateTotalBalance()
        }
    }
}