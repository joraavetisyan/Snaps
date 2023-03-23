package io.snaps.featurecollection.data

import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.State
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.model.WalletAddress
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.database.UserDataStorage
import io.snaps.coredata.network.apiCall
import io.snaps.coreui.viewmodel.tryPublish
import io.snaps.featurecollection.data.model.MintNftRequestDto
import io.snaps.featurecollection.domain.NftModel
import io.snaps.featurecollection.domain.RankModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

interface MyCollectionRepository {

    val nftCollectionState: StateFlow<State<List<NftModel>>>

    val mysteryBoxCollectionState: StateFlow<State<List<NftModel>>>

    val ranksState: StateFlow<State<List<RankModel>>>

    suspend fun loadRanks(): Effect<Completable>

    suspend fun loadNftCollection(): Effect<Completable>

    suspend fun loadMysteryBoxCollection(): Effect<Completable>

    suspend fun mintNft(
        type: NftType,
        purchaseId: Uuid,
        walletAddress: WalletAddress,
    ): Effect<Int>
}

class MyCollectionRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val myCollectionApi: MyCollectionApi,
    private val userDataStorage: UserDataStorage,
) : MyCollectionRepository {

    private val _nftCollectionState = MutableStateFlow<State<List<NftModel>>>(Loading())
    override val nftCollectionState = _nftCollectionState.asStateFlow()

    private val _mysteryBoxCollectionState = MutableStateFlow<State<List<NftModel>>>(Loading())
    override val mysteryBoxCollectionState = _mysteryBoxCollectionState.asStateFlow()

    private val _ranksState = MutableStateFlow<State<List<RankModel>>>(Loading())
    override val ranksState = _ranksState.asStateFlow()

    override suspend fun loadRanks(): Effect<Completable> {
        return apiCall(ioDispatcher) {
            myCollectionApi.nft()
        }.map {
            it.toRankModelList()
        }.also {
            _ranksState tryPublish it
        }.toCompletable()
    }

    override suspend fun loadNftCollection(): Effect<Completable> {
        return apiCall(ioDispatcher) {
            myCollectionApi.userNftCollection()
        }.map {
            it.toNftModelList()
        }.also {
            _nftCollectionState tryPublish it
        }.toCompletable()
    }

    override suspend fun loadMysteryBoxCollection(): Effect<Completable> {
        return apiCall(ioDispatcher) {
            myCollectionApi.userNftCollection()
        }.map {
            it.toNftModelList()
        }.also {
            _mysteryBoxCollectionState tryPublish it
        }.toCompletable()
    }

    override suspend fun mintNft(
        type: NftType,
        purchaseId: Uuid,
        walletAddress: WalletAddress,
    ): Effect<Int> {
        return apiCall(ioDispatcher) {
            myCollectionApi.mintNft(
                body = MintNftRequestDto(
                    nftType = type.intType,
                    purchaseId = purchaseId,
                    wallet = walletAddress,
                ),
            )
        }.doOnSuccess {
            userDataStorage.hasNft = true
            loadNftCollection()
            loadRanks()
        }.map {
            it.tokenId
        }
    }
}