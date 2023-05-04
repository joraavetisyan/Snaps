package io.snaps.basenft.data

import io.snaps.basenft.data.model.MintNftRequestDto
import io.snaps.basenft.data.model.RepairGlassesRequestDto
import io.snaps.basenft.domain.RankModel
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.NftModel
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.State
import io.snaps.corecommon.model.Token
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.model.WalletAddress
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.database.UserDataStorage
import io.snaps.coredata.network.apiCall
import io.snaps.coreui.viewmodel.likeStateFlow
import io.snaps.coreui.viewmodel.tryPublish
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface NftRepository {

    val nftCollectionState: StateFlow<State<List<NftModel>>>

    val ranksState: StateFlow<State<List<RankModel>>>

    val countBrokenGlassesState: StateFlow<State<Int>>

    suspend fun updateRanks(): Effect<Completable>

    suspend fun updateNftCollection(): Effect<List<NftModel>>

    /**
     * params: [data] - Google Play purchaseToken or blockchain transaction hash
     */
    suspend fun mintNft(
        type: NftType,
        walletAddress: WalletAddress,
        data: Token?,
    ): Effect<Completable>

    /**
     * params: [data] - Blockchain transaction hash
     */
    suspend fun repairNft(
        nftModel: NftModel,
        data: Token? = null,
        offChainAmount: Long = 0L,
    ): Effect<Completable>

    suspend fun saveProcessingNft(nftType: NftType): Effect<Completable>
}

class NftRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    private val nftApi: NftApi,
    private val userDataStorage: UserDataStorage,
) : NftRepository {

    private val _nftCollectionState = MutableStateFlow<State<List<NftModel>>>(Loading())
    override val nftCollectionState = _nftCollectionState.asStateFlow()

    private val _ranksState = MutableStateFlow<State<List<RankModel>>>(Loading())
    override val ranksState = _ranksState.asStateFlow()

    override val countBrokenGlassesState = nftCollectionState.map {
        when (it) {
            is Loading -> Loading()
            is Effect -> when {
                it.isSuccess -> Effect.success(
                    it.requireData.count { nft -> !nft.isHealthy }
                )

                else -> Effect.error(requireNotNull(it.errorOrNull))
            }
        }
    }.likeStateFlow(scope, Loading())

    override suspend fun updateRanks(): Effect<Completable> {
        return apiCall(ioDispatcher) {
            nftApi.nft()
        }.map {
            it.toRankModelList()
        }.also {
            _ranksState tryPublish it
        }.toCompletable()
    }

    override suspend fun updateNftCollection(): Effect<List<NftModel>> {
        return apiCall(ioDispatcher) {
            nftApi.userNftCollection()
        }.map {
            it.toNftModelList()
        }.also {
            _nftCollectionState tryPublish it
        }
    }

    override suspend fun mintNft(
        type: NftType,
        walletAddress: WalletAddress,
        data: Token?,
    ): Effect<Completable> {
        return apiCall(ioDispatcher) {
            nftApi.mintNft(
                body = MintNftRequestDto(
                    nftType = type.intType,
                    purchaseId = data,
                    wallet = walletAddress,
                ),
            )
        }.doOnSuccess {
            updateNftCollection()
            updateRanks()
        }.toCompletable()
    }

    override suspend fun repairNft(
        nftModel: NftModel,
        data: Token?,
        offChainAmount: Long,
    ): Effect<Completable> {
        return apiCall(ioDispatcher) {
            nftApi.repairGlasses(
                body = RepairGlassesRequestDto(
                    glassesId = nftModel.id,
                    offChainAmount = offChainAmount,
                    transactionHash = data,
                )
            )
        }.doOnSuccess {
            updateNftCollection()
        }
    }

    override suspend fun saveProcessingNft(nftType: NftType): Effect<Completable> {

        return Effect.completable
    }
}