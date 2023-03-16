package io.snaps.featurecollection.data

import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.date.toOffsetLocalDateTime
import io.snaps.corecommon.mock.rBool
import io.snaps.corecommon.mock.rDouble
import io.snaps.corecommon.mock.rImage
import io.snaps.corecommon.mock.rInt
import io.snaps.featurecollection.data.model.NftItemResponseDto
import io.snaps.featurecollection.data.model.UserNftItemResponseDto
import io.snaps.featurecollection.domain.NftModel
import io.snaps.featurecollection.domain.RankModel
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