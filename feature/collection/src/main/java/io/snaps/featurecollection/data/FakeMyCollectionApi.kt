package io.snaps.featurecollection.data

import io.snaps.corecommon.ext.log
import io.snaps.corecommon.mock.mockDelay
import io.snaps.corecommon.mock.rBool
import io.snaps.coredata.network.BaseResponse
import io.snaps.featurecollection.data.model.NftItemDto
import io.snaps.featurecollection.data.model.NftResponseDto
import io.snaps.featurecollection.data.model.RankItemResponseDto
import kotlinx.coroutines.delay

class FakeMyCollectionApi : MyCollectionApi {

    private var generation = 0

    override suspend fun nftCollection(): BaseResponse<NftResponseDto> {
        log("Requesting nft")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = NftResponseDto(
                maxCount = 4,
                items = getRanks()
                    .filter { it.isSelected }
                    .map {
                        NftItemDto(
                            id = it.id,
                            type = it.type,
                            image = it.image,
                            price = it.price,
                            dailyConsumption = it.dailyConsumption,
                            dailyUnlock = it.dailyUnlock,
                            dailyReward = it.dailyReward,
                        )
                }
            )
        )
    }

    override suspend fun mysteryBoxCollection(): BaseResponse<NftResponseDto> {
        log("Requesting mystery box")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = NftResponseDto(
                maxCount = 4,
                items = getRanks()
                    .filter { it.isSelected }
                    .map {
                        NftItemDto(
                            id = it.id,
                            type = it.type,
                            image = it.image,
                            price = it.price,
                            dailyConsumption = it.dailyConsumption,
                            dailyUnlock = it.dailyUnlock,
                            dailyReward = it.dailyReward,
                        )
                    }
            )
        )
    }

    override suspend fun ranks(): BaseResponse<List<RankItemResponseDto>> {
        log("Requesting ranks")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = getRanks()
        ).also { generation++ }
    }

    private fun getRanks() = listOf(
        RankItemResponseDto(
            id = "${generation}rank",
            type = "Free",
            price = "Free",
            image = "https://picsum.photos/200",
            dailyReward = "0.5$",
            dailyUnlock = "6.66%",
            dailyConsumption = "57%",
            isSelected = rBool,
        ),
        RankItemResponseDto(
            id = "${generation}rank",
            type = "Newbee",
            price = "60$",
            image = "https://picsum.photos/200",
            dailyReward = "0.5$",
            dailyUnlock = "6.66%",
            dailyConsumption = "57%",
            isSelected = rBool,
        ),
        RankItemResponseDto(
            id = "${generation}rank",
            type = "Viewer",
            price = "60$",
            image = "https://picsum.photos/200",
            dailyReward = "0.5$",
            dailyUnlock = "6.66%",
            dailyConsumption = "57%",
            isSelected = rBool,
        ),
        RankItemResponseDto(
            id = "${generation}rank",
            type = "Follower",
            price = "60$",
            image = "https://picsum.photos/200",
            dailyReward = "0.5$",
            dailyUnlock = "6.66%",
            dailyConsumption = "57%",
            isSelected = rBool,
        ),
    )
}