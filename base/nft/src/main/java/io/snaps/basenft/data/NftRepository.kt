package io.snaps.basenft.data

import io.snaps.basenft.domain.NftModel
import io.snaps.basenft.domain.RankModel
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

interface NftRepository {

    val nftCollectionState: StateFlow<State<List<NftModel>>>

    val ranksState: StateFlow<State<List<RankModel>>>

    suspend fun updateRanks(): Effect<Completable>

    suspend fun updateNftCollection(): Effect<Completable>

    suspend fun mintNft(
        type: NftType,
        purchaseId: Uuid?,
        walletAddress: WalletAddress,
    ): Effect<Completable>
}

class NftRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val nftApi: NftApi,
    private val userDataStorage: UserDataStorage,
) : NftRepository {

    private val _nftCollectionState = MutableStateFlow<State<List<NftModel>>>(Loading())
    override val nftCollectionState = _nftCollectionState.asStateFlow()

    private val _ranksState = MutableStateFlow<State<List<RankModel>>>(Loading())
    override val ranksState = _ranksState.asStateFlow()

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
}