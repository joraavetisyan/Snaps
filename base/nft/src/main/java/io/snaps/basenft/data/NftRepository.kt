package io.snaps.basenft.data

import io.snaps.basenft.data.model.RepairGlassesRequestDto
import io.snaps.basenft.domain.NftModel
import io.snaps.basenft.domain.RankModel
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.State
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

    suspend fun updateNftCollection(): Effect<Completable>

    suspend fun mintNft(
        type: NftType,
        purchaseId: Uuid?,
        walletAddress: WalletAddress,
    ): Effect<Completable>

    suspend fun repairGlasses(
        glassesId: Uuid,
    ): Effect<Completable>
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

    override suspend fun updateNftCollection(): Effect<Completable> {
        return apiCall(ioDispatcher) {
            nftApi.userNftCollection()
        }.map {
            it.toNftModelList()
        }.also {
            _nftCollectionState tryPublish it
        }.toCompletable()
    }

    override suspend fun mintNft(
        type: NftType,
        purchaseId: Uuid?,
        walletAddress: WalletAddress,
    ): Effect<Completable> {
        return apiCall(ioDispatcher) {
            nftApi.mintNft(
                body = io.snaps.basenft.data.model.MintNftRequestDto(
                    nftType = type.intType,
                    purchaseId = purchaseId,
                    wallet = walletAddress,
                ),
            )
        }.doOnSuccess {
            userDataStorage.hasNft = true
            updateNftCollection()
            updateRanks()
        }.toCompletable()
    }

    override suspend fun repairGlasses(glassesId: Uuid): Effect<Completable> {
        return apiCall(ioDispatcher) {
            nftApi.repairGlasses(
                body = RepairGlassesRequestDto(glassesId),
            )
        }.doOnSuccess {
            updateNftCollection()
        }
    }
}