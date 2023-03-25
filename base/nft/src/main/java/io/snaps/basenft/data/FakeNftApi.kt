package io.snaps.basenft.data

import io.snaps.basenft.data.model.MintNftRequestDto
import io.snaps.basenft.data.model.MintNftResponseDto
import io.snaps.basenft.data.model.NftItemResponseDto
import io.snaps.basenft.data.model.RepairGlassesRequestDto
import io.snaps.basenft.data.model.UserNftItemResponseDto
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.mock.mockDelay
import io.snaps.corecommon.mock.rBool
import io.snaps.corecommon.mock.rDouble
import io.snaps.corecommon.mock.rImage
import io.snaps.corecommon.mock.rInt
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.NftType
import io.snaps.coredata.network.BaseResponse
import kotlinx.coroutines.delay

class FakeNftApi : NftApi {

    private var generation = 0

    override suspend fun userNftCollection(): BaseResponse<List<UserNftItemResponseDto>> {
        log("Requesting nft")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = getUserNft()
        ).also {
            generation++
        }
    }

    override suspend fun nft(): BaseResponse<List<NftItemResponseDto>> {
        log("Requesting ranks")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = getRanks()
        )
    }

    override suspend fun mintNft(body: MintNftRequestDto): BaseResponse<MintNftResponseDto> {
        log("Requesting add nft")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = MintNftResponseDto(
                tokenId = rInt,
            ),
        )
    }

    override suspend fun repairGlasses(body: RepairGlassesRequestDto): BaseResponse<Completable> {
        log("Requesting repair Glasses")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = Completable,
        )
    }

    private fun getRanks() = buildList {
        NftType.values().forEach {
            add(
                NftItemResponseDto(
                    type = it,
                    dailyReward = rInt,
                    dailyUnlock = rDouble,
                    dailyConsumption = rDouble,
                    costInUsd = rInt,
                    costInRealTokens = rInt,
                    isAvailableToPurchase = rBool,
                    pathToImage = rImage,
                )
            )
        }
    }

    private fun getUserNft() = listOf(
        UserNftItemResponseDto(
            id = "$generation nft",
            userId = "$generation userId",
            tokenId = null,
            type = getRanks()[0],
            mintedDate = "2023-02-07T02:46:30.3218237+00:00",
            isHealthy = false,
        ),
        UserNftItemResponseDto(
            id = "$generation nft",
            userId = "$generation userId",
            tokenId = null,
            type = getRanks()[1],
            mintedDate = "2023-02-07T02:46:30.3218237+00:00",
            isHealthy = false,
        ),
        UserNftItemResponseDto(
            id = "$generation nft",
            userId = "$generation userId",
            tokenId = null,
            type = getRanks()[2],
            mintedDate = "2023-02-07T02:46:30.3218237+00:00",
            isHealthy = false,
        ),
    )
}