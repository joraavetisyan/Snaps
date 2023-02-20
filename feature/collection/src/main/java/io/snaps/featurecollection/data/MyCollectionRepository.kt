package io.snaps.featurecollection.data

import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.apiCall
import io.snaps.coreui.viewmodel.tryPublish
import io.snaps.featurecollection.domain.NftModel
import io.snaps.featurecollection.domain.RankModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

interface MyCollectionRepository {

    val nftCollectionState: StateFlow<State<NftModel>>

    val mysteryBoxCollectionState: StateFlow<State<NftModel>>

    suspend fun getRanks(): Effect<List<RankModel>>

    suspend fun loadNftCollection(): Effect<Completable>

    suspend fun loadMysteryBoxCollection(): Effect<Completable>

    suspend fun addNft(rankId: Uuid): Effect<Completable>
}

class MyCollectionRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val myCollectionApi: MyCollectionApi,
) : MyCollectionRepository {

    private val _nftCollectionState = MutableStateFlow<State<NftModel>>(Loading())
    override val nftCollectionState = _nftCollectionState.asStateFlow()

    private val _mysteryBoxCollectionState = MutableStateFlow<State<NftModel>>(Loading())
    override val mysteryBoxCollectionState = _mysteryBoxCollectionState.asStateFlow()

    override suspend fun getRanks(): Effect<List<RankModel>> {
        return apiCall(ioDispatcher) {
            myCollectionApi.ranks()
        }.map {
            it.toModelList()
        }
    }

    override suspend fun loadNftCollection(): Effect<Completable> {
        return apiCall(ioDispatcher) {
            myCollectionApi.nftCollection()
        }.map {
            it.toModel()
        }.also {
            _nftCollectionState tryPublish it
        }.toCompletable()
    }

    override suspend fun loadMysteryBoxCollection(): Effect<Completable> {
        return apiCall(ioDispatcher) {
            myCollectionApi.nftCollection()
        }.map {
            it.toModel()
        }.also {
            _mysteryBoxCollectionState tryPublish it
        }.toCompletable()
    }

    override suspend fun addNft(rankId: Uuid): Effect<Completable> {
        return apiCall(ioDispatcher) {
            myCollectionApi.addNft(rankId)
        }
    }
}