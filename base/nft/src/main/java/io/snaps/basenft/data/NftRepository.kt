package io.snaps.basenft.data

import io.snaps.basenft.data.model.MintNftRequestDto
import io.snaps.basenft.data.model.MintNftStoreRequestDto
import io.snaps.basenft.data.model.RepairGlassesRequestDto
import io.snaps.basenft.domain.NftModel
import io.snaps.basenft.domain.RankModel
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.State
import io.snaps.corecommon.model.Token
import io.snaps.corecommon.model.TxHash
import io.snaps.corecommon.model.TxSign
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.coroutine.UserSessionCoroutineScope
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

    val allGlassesBrokenState: StateFlow<State<Boolean>>

    suspend fun updateRanks(): Effect<Completable>

    suspend fun updateNftCollection(): Effect<List<NftModel>>

    suspend fun mintNftStore(productId: Uuid, purchaseToken: Token, txSign: TxSign): Effect<TxHash>

    /**
     * [txSign]=null for [NftType.Free]
     */
    suspend fun mintNft(type: NftType, txSign: TxSign? = null): Effect<TxHash>

    suspend fun repairNftBlockchain(nftModel: NftModel, txSign: TxSign): Effect<TxHash>

    suspend fun repairNft(nftModel: NftModel): Effect<Completable>
}

class NftRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @UserSessionCoroutineScope private val scope: CoroutineScope,
    private val nftApi: NftApi,
) : NftRepository {

    private val _nftCollectionState = MutableStateFlow<State<List<NftModel>>>(Loading())
    override val nftCollectionState = _nftCollectionState.asStateFlow()

    private val _ranksState = MutableStateFlow<State<List<RankModel>>>(Loading())
    override val ranksState = _ranksState.asStateFlow()

    override val countBrokenGlassesState = nftCollectionState.map { state ->
        when (state) {
            is Loading -> Loading()
            is Effect -> when {
                state.isSuccess -> Effect.success(state.requireData.count { !it.isHealthy })
                else -> Effect.error(requireNotNull(state.errorOrNull))
            }
        }
    }.likeStateFlow(scope, Loading())

    override val allGlassesBrokenState = nftCollectionState.map { state ->
        when (state) {
            is Loading -> Loading()
            is Effect -> when {
                state.isSuccess -> Effect.success(state.requireData.all { !it.isHealthy })
                else -> Effect.error(requireNotNull(state.errorOrNull))
            }
        }
    }.likeStateFlow(scope, Loading())

    override suspend fun updateRanks(): Effect<Completable> {
        return apiCall(ioDispatcher) {
            nftApi.getNfts()
        }.map {
            it.toRankModelList()
        }.also {
            _ranksState tryPublish it
        }.toCompletable()
    }

    override suspend fun updateNftCollection(): Effect<List<NftModel>> {
        return apiCall(ioDispatcher) {
            nftApi.getUserNftCollection()
        }.map {
            it.toNftModelList()
        }.also {
            _nftCollectionState tryPublish it
        }
    }

    override suspend fun mintNft(type: NftType, txSign: TxHash?): Effect<TxHash> {
        return apiCall(ioDispatcher) {
            nftApi.mintNft(MintNftRequestDto(nftType = type, txSign = txSign))
        }.doOnSuccess {
            updateNftCollection()
            updateRanks()
        }.map {
            it.txHash.orEmpty()
        }
    }

    /**
     * For purchases through Google Play
     */
    override suspend fun mintNftStore(productId: Uuid, purchaseToken: Token, txSign: TxSign): Effect<TxHash> {
        return apiCall(ioDispatcher) {
            nftApi.mintNftStore(
                body = MintNftStoreRequestDto(
                    productId = productId,
                    purchaseToken = purchaseToken,
                    txSign = txSign,
                ),
            )
        }.doOnSuccess {
            updateNftCollection()
            updateRanks()
        }.map { it.txHash.orEmpty() }
    }

    override suspend fun repairNftBlockchain(nftModel: NftModel, txSign: TxSign): Effect<TxHash> {
        return apiCall(ioDispatcher) {
            nftApi.repairGlasses(RepairGlassesRequestDto(glassesId = nftModel.id, txSign = txSign))
        }.doOnSuccess {
            updateNftCollection()
        }.map { it.txHash.orEmpty() }
    }

    override suspend fun repairNft(nftModel: NftModel): Effect<Completable> {
        return apiCall(ioDispatcher) {
            nftApi.repairGlasses(RepairGlassesRequestDto(glassesId = nftModel.id))
        }.doOnSuccess {
            updateNftCollection()
        }.toCompletable()
    }
}