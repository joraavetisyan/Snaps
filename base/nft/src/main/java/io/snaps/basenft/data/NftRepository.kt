package io.snaps.basenft.data

import io.snaps.basenft.data.model.MintNftRequestDto
import io.snaps.basenft.data.model.MintNftStoreRequestDto
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
import java.util.Collections.max
import javax.inject.Inject
import kotlin.math.max

interface NftRepository {

    val nftCollectionState: StateFlow<State<List<NftModel>>>

    val ranksState: StateFlow<State<List<RankModel>>>

    val countBrokenGlassesState: StateFlow<State<Int>>

    suspend fun updateRanks(): Effect<Completable>

    suspend fun updateNftCollection(): Effect<List<NftModel>>

    suspend fun mintNftStore(productId: Uuid, purchaseToken: Token): Effect<Completable>

    /**
     * params: [transactionHash] - Blockchain transaction hash
     */
    suspend fun mintNft(type: NftType, transactionHash: Token? = null): Effect<Completable>

    /**
     * params: [transactionHash] - Blockchain transaction hash
     */
    suspend fun repairNft(
        nftModel: NftModel,
        transactionHash: Token? = null,
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
        }.map { dtoList ->
            dtoList.toNftModelList()
                .groupBy(NftModel::type)
                .mapValues { (type, list) ->
                    val processingCount = userDataStorage.getProcessingNftCount(type)
                    val overCount = (userDataStorage.getProcessingNftCount(type) - list.size).coerceAtLeast(0)
                    userDataStorage.setProcessingNftCount(type, max(processingCount, list.size))
                    list + List(overCount) { i -> list.first().let {
                        it.copy(id = it.id + "processing$i", isProcessing = true) }
                    }
                }
                .flatMap(Map.Entry<NftType, List<NftModel>>::value)
        }.also {
            _nftCollectionState tryPublish it
        }
    }

    override suspend fun mintNft(type: NftType, transactionHash: Token?): Effect<Completable> {
        return apiCall(ioDispatcher) {
            nftApi.mintNft(
                body = MintNftRequestDto(
                    nftType = type.intType,
                    transactionHash = transactionHash,
                ),
            )
        }.doOnSuccess {
            updateNftCollection()
            updateRanks()
        }.toCompletable()
    }

    override suspend fun mintNftStore(productId: Uuid, purchaseToken: Token): Effect<Completable> {
        return apiCall(ioDispatcher) {
            nftApi.mintNftStore(
                body = MintNftStoreRequestDto(
                    productId = productId,
                    purchaseToken = purchaseToken,
                ),
            )
        }.doOnSuccess {
            updateNftCollection()
            updateRanks()
        }.toCompletable()
    }

    override suspend fun repairNft(
        nftModel: NftModel,
        transactionHash: Token?,
        offChainAmount: Long,
    ): Effect<Completable> {
        return apiCall(ioDispatcher) {
            nftApi.repairGlasses(
                body = RepairGlassesRequestDto(
                    glassesId = nftModel.id,
                    offChainAmount = offChainAmount,
                    transactionHash = transactionHash,
                )
            )
        }.doOnSuccess {
            updateNftCollection()
        }
    }

    override suspend fun saveProcessingNft(nftType: NftType): Effect<Completable> {
        userDataStorage.setProcessingNftCount(
            type = nftType,
            totalCount = userDataStorage.getProcessingNftCount(nftType) + 1,
        )
        return Effect.completable
    }
}