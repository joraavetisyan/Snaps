package io.snaps.basenft.data

import io.snaps.basenft.data.model.MintNftRequestDto
import io.snaps.basenft.data.model.MintNftStoreRequestDto
import io.snaps.basenft.data.model.RepairGlassesRequestDto
import io.snaps.basenft.domain.RankModel
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.NftModel
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.State
import io.snaps.corecommon.model.Token
import io.snaps.corecommon.model.TxHash
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.database.UserDataStorage
import io.snaps.coredata.json.KotlinxSerializationJsonProvider
import io.snaps.coredata.network.apiCall
import io.snaps.coreui.viewmodel.likeStateFlow
import io.snaps.coreui.viewmodel.tryPublish
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.max

interface NftRepository {

    val nftCollectionState: StateFlow<State<List<NftModel>>>

    val ranksState: StateFlow<State<List<RankModel>>>

    val countBrokenGlassesState: StateFlow<State<Int>>

    suspend fun updateRanks(): Effect<Completable>

    suspend fun updateNftCollection(): Effect<List<NftModel>>

    suspend fun mintNftStore(productId: Uuid, purchaseToken: Token): Effect<Completable>

    suspend fun mintNft(type: NftType, transactionHash: TxHash? = null): Effect<Completable>

    suspend fun repairNft(
        nftModel: NftModel,
        transactionHash: TxHash? = null,
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

    init {
        scope.launch {
            repairPreviouslyFailedNfts()
            mintPreviouslyFailedNfts()
        }
    }

    private suspend fun repairPreviouslyFailedNfts() {
        userDataStorage.getNonRepairedNfts().forEach {
            val decoded = KotlinxSerializationJsonProvider().get().decodeFromString<RepairGlassesRequestDto>(it)
            log("Repairing previously failed nft $decoded")
            repairNft(decoded)
        }
    }

    private suspend fun mintPreviouslyFailedNfts() {
        userDataStorage.getNonMintedNfts().forEach {
            val decoded = KotlinxSerializationJsonProvider().get().decodeFromString<MintNftRequestDto>(it)
            log("Minting previously failed nft $decoded")
            mintNft(decoded)
        }
    }

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
                .let { map ->
                    val nonAdded = NftType.values().filter {
                        !map.keys.contains(it)
                    }
                    map.toMutableMap().apply {
                        putAll(nonAdded.associateWith { emptyList() })
                    }
                }
                .mapValues { (type, list) ->
                    val processingCount = userDataStorage.getProcessingNftCount(type)
                    val overCount = (userDataStorage.getProcessingNftCount(type) - list.size).coerceAtLeast(0)
                    userDataStorage.setProcessingNftCount(type, max(processingCount, list.size))
                    list + List(overCount) { i ->
                        list.firstOrNull()?.let {
                            it.copy(id = it.id + "processing$i", isProcessing = true)
                        } ?: NftModel(
                            id = "${type.name}processing$i",
                            tokenId = "",
                            userId = "",
                            type = type,
                            image = type.getSunglassesImage(),
                            dailyReward = 0,
                            dailyUnlock = 0.0,
                            dailyConsumption = 0.0,
                            isAvailableToPurchase = false,
                            costInUsd = 0,
                            costInRealTokens = 0,
                            mintedDate = LocalDateTime.now(),
                            isHealthy = true,
                            repairCost = 0.0,
                            isProcessing = true,
                            level = 0,
                            experience = 0,
                            lowerThreshold = 0,
                            upperThreshold = 0,
                            bonus = 0,
                        )
                    }
                }
                .flatMap(Map.Entry<NftType, List<NftModel>>::value)
        }.also {
            _nftCollectionState tryPublish it
        }
    }

    override suspend fun mintNft(type: NftType, transactionHash: TxHash?): Effect<Completable> {
        return mintNft(
            body = MintNftRequestDto(
                nftType = type,
                transactionHash = transactionHash,
            ),
        )
    }

    private suspend fun mintNft(body: MintNftRequestDto): Effect<Completable> {
        val encoded = KotlinxSerializationJsonProvider().get().encodeToString(body)
        return apiCall(ioDispatcher) {
            nftApi.mintNft(body)
        }.doOnSuccess {
            userDataStorage.removeNonMintedNft(encoded)
            updateNftCollection()
            updateRanks()
        }.doOnError { _, _ ->
            userDataStorage.setNonMintedNft(encoded)
        }.toCompletable()
    }

    /**
     * For purchases through Google Play
     */
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
        transactionHash: TxHash?,
        offChainAmount: Long,
    ): Effect<Completable> {
        return repairNft(
            body = RepairGlassesRequestDto(
                glassesId = nftModel.id,
                offChainAmount = offChainAmount,
                transactionHash = transactionHash,
            ),
        )
    }

    private suspend fun repairNft(body: RepairGlassesRequestDto): Effect<Completable> {
        val encoded = KotlinxSerializationJsonProvider().get().encodeToString(body)
        return apiCall(ioDispatcher) {
            nftApi.repairGlasses(body = body)
        }.doOnSuccess {
            userDataStorage.removeNonRepairedNft(encoded)
            updateNftCollection()
        }.doOnError { _, _ ->
            userDataStorage.setNonRepairedNft(encoded)
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