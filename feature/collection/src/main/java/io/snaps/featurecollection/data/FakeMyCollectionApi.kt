package io.snaps.featurecollection.data

import io.snaps.corecommon.ext.log
import io.snaps.corecommon.mock.mockDelay
import io.snaps.corecommon.mock.rBool
import io.snaps.corecommon.mock.rDouble
import io.snaps.corecommon.mock.rImage
import io.snaps.corecommon.mock.rInt
import io.snaps.corecommon.model.NftType
import io.snaps.coredata.network.BaseResponse
import io.snaps.featurecollection.data.model.MintNftRequestDto
import io.snaps.featurecollection.data.model.MintNftResponseDto
import io.snaps.featurecollection.data.model.NftItemResponseDto
import io.snaps.featurecollection.data.model.UserNftItemResponseDto
import kotlinx.coroutines.delay

class FakeMyCollectionApi : MyCollectionApi {

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

    override suspend fun mysteryBoxCollection(): BaseResponse<List<UserNftItemResponseDto>> {
        log("Requesting mystery box")
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
            mintedDate = "",
            isHealthy = false,
        ),
        UserNftItemResponseDto(
            id = "$generation nft",
            userId = "$generation userId",
            tokenId = null,
            type = getRanks()[1],
            mintedDate = "",
            isHealthy = false,
        ),
        UserNftItemResponseDto(
            id = "$generation nft",
            userId = "$generation userId",
            tokenId = null,
            type = getRanks()[2],
            mintedDate = "",
            isHealthy = false,
        ),
    )
}