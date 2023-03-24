package io.snaps.basenft.data

import io.snaps.basenft.data.model.NftItemResponseDto
import io.snaps.basenft.data.model.UserNftItemResponseDto
import io.snaps.basenft.domain.NftModel
import io.snaps.basenft.domain.RankModel
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.date.toOffsetLocalDateTime
import java.time.ZonedDateTime

fun List<NftItemResponseDto>.toRankModelList() = map(NftItemResponseDto::toModel)

private fun NftItemResponseDto.toModel() = RankModel(
    type = type,
    costInUsd = costInUsd,
    costInRealTokens = costInRealTokens,
    image = ImageValue.Url(pathToImage),
    dailyReward = dailyReward,
    dailyUnlock = dailyUnlock,
    dailyConsumption = dailyConsumption,
    isAvailableToPurchase = isAvailableToPurchase,
)

fun List<UserNftItemResponseDto>.toNftModelList() = map(UserNftItemResponseDto::toModel)

private fun UserNftItemResponseDto.toModel() = NftModel(
    id = id,
    userId = userId,
    tokenId = tokenId,
    isHealthy = isHealthy,
    mintedDate = requireNotNull(ZonedDateTime.parse(mintedDate)).toOffsetLocalDateTime(),
    type = type.type,
    costInUsd = type.costInUsd,
    costInRealTokens = type.costInRealTokens,
    image = ImageValue.Url(type.pathToImage),
    dailyReward = type.dailyReward,
    dailyUnlock = type.dailyUnlock,
    dailyConsumption = type.dailyConsumption,
    isAvailableToPurchase = type.isAvailableToPurchase,
)