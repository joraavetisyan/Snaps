package io.snaps.basenft.data

import io.snaps.basenft.data.model.BundleItemResponseDto
import io.snaps.basenft.data.model.LevelInfo
import io.snaps.basenft.data.model.MintBundleResponseDto
import io.snaps.basenft.data.model.MintMysteryBoxRequestDto
import io.snaps.basenft.data.model.MintMysteryBoxResponseDto
import io.snaps.basenft.data.model.MintNftRequestDto
import io.snaps.basenft.data.model.MintNftResponseDto
import io.snaps.basenft.data.model.MintNftStoreRequestDto
import io.snaps.basenft.data.model.MysteryBoxItemResponseDto
import io.snaps.basenft.data.model.NftItemAdditionalDataDto
import io.snaps.basenft.data.model.NftItemResponseDto
import io.snaps.basenft.data.model.ProbabilitiesDto
import io.snaps.basenft.data.model.RepairGlassesRequestDto
import io.snaps.basenft.data.model.RepairGlassesResponseDto
import io.snaps.basenft.data.model.UserNftItemResponseDto
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.mock.mockDelay
import io.snaps.corecommon.mock.rBool
import io.snaps.corecommon.mock.rDouble
import io.snaps.corecommon.mock.rImage
import io.snaps.corecommon.mock.rInt
import io.snaps.corecommon.model.MysteryBoxType
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.BaseResponse
import kotlinx.coroutines.delay

class FakeNftApi : NftApi {

    private var generation = 0

    override suspend fun getCurrentUserNftCollection(): BaseResponse<List<UserNftItemResponseDto>> {
        log("Requesting current user nft")
        delay(mockDelay)
        return BaseResponse(
            data = getUserNft()
        ).also {
            generation++
        }
    }

    override suspend fun getNfts(): BaseResponse<List<NftItemResponseDto>> {
        log("Requesting ranks")
        delay(mockDelay)
        return BaseResponse(
            data = getRanks()
        )
    }

    override suspend fun mintNft(body: MintNftRequestDto): BaseResponse<MintNftResponseDto> {
        log("Requesting add nft")
        delay(mockDelay)
        return BaseResponse(
            data = MintNftResponseDto(
                txHash = "",
            ),
        )
    }

    override suspend fun mintMysteryBox(body: MintMysteryBoxRequestDto): BaseResponse<MintMysteryBoxResponseDto> {
        log("Requesting add mystery box")
        delay(mockDelay)
        return BaseResponse(
            data = MintMysteryBoxResponseDto(
                txHash = "",
                nftTypeFromBox = NftType.Sponsor,
            ),
        )
    }

    override suspend fun mintNftStore(body: MintNftStoreRequestDto): BaseResponse<MintNftResponseDto> {
        log("Requesting add nft on store")
        delay(mockDelay)
        return BaseResponse(
            data = MintNftResponseDto(
                txHash = "",
            ),
        )
    }

    override suspend fun repairGlasses(body: RepairGlassesRequestDto): BaseResponse<RepairGlassesResponseDto> {
        log("Requesting repair Glasses")
        delay(mockDelay)
        return BaseResponse(
            data = RepairGlassesResponseDto(null),
        )
    }

    override suspend fun getMysteryBoxes(): BaseResponse<List<MysteryBoxItemResponseDto>> {
        log("Requesting mystery box")
        delay(mockDelay)
        return BaseResponse(
            data = listOf(
                MysteryBoxItemResponseDto(
                    type = MysteryBoxType.FirstTier,
                    costInUsd = rDouble,
                    marketingProbabilities = ProbabilitiesDto(
                        follower = null,
                        sub = null,
                        sponsor = null,
                        influencer = null,
                        rockstar = rDouble,
                        famousGuy = rDouble,
                        newbie = null,
                        superStar = null,
                        viewer = rDouble,
                        blogger = null,
                        legend = null,
                        star = rDouble,
                    ),
                )
            ),
        )
    }

    override suspend fun getUserNftCollection(userId: Uuid): BaseResponse<List<UserNftItemResponseDto>> {
        TODO("Not yet implemented")
    }

    override suspend fun getBundles(): BaseResponse<List<BundleItemResponseDto>> {
        TODO("Not yet implemented")
    }

    override suspend fun mintBundle(body: MintMysteryBoxRequestDto): BaseResponse<MintBundleResponseDto> {
        TODO("Not yet implemented")
    }

    private fun getRanks() = buildList {
        NftType.values().forEach {
            add(
                NftItemResponseDto(
                    type = it,
                    dailyReward = rInt,
                    percentGrowingPerDay = rDouble,
                    dailyMaintenanceCostMultiplier = rDouble,
                    costInUsd = rDouble,
                    costInRealTokens = rInt,
                    isAvailableToPurchase = rBool,
                    pathToImage = rImage,
                    additionalData = NftItemAdditionalDataDto(1),
                )
            )
        }
    }

    private fun getUserNft() = listOf(
        UserNftItemResponseDto(
            id = "$generation nft",
            userId = "$generation userId",
            tokenId = null,
            data = getRanks()[0],
            mintedDate = "2023-02-07T02:46:30.3218237+00:00",
            isHealthy = false,
            levelInfo = LevelInfo(
                level = 0,
                experience = 0,
                lowerThreshold = 0,
                upperThreshold = 0,
                bonus = 0.0,
            ),
        ),
        UserNftItemResponseDto(
            id = "$generation nft",
            userId = "$generation userId",
            tokenId = null,
            data = getRanks()[1],
            mintedDate = "2023-02-07T02:46:30.3218237+00:00",
            isHealthy = false,
            levelInfo = LevelInfo(
                level = 0,
                experience = 0,
                lowerThreshold = 0,
                upperThreshold = 0,
                bonus = 0.0,
            ),
        ),
        UserNftItemResponseDto(
            id = "$generation nft",
            userId = "$generation userId",
            tokenId = null,
            data = getRanks()[2],
            mintedDate = "2023-02-07T02:46:30.3218237+00:00",
            isHealthy = false,
            levelInfo = LevelInfo(
                level = 0,
                experience = 0,
                lowerThreshold = 0,
                upperThreshold = 0,
                bonus = 0.0,
            ),
        ),
    )
}