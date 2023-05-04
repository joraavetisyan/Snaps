package io.snaps.basenft.data

import io.snaps.basenft.data.model.NftItemResponseDto
import io.snaps.basenft.data.model.UserNftItemResponseDto
import io.snaps.corecommon.model.NftModel
import io.snaps.basenft.domain.RankModel
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.date.toOffsetLocalDateTime
import java.time.ZonedDateTime

fun List<NftItemResponseDto>.toRankModelList() = map(NftItemResponseDto::toModel)

private fun NftItemResponseDto.toModel() = RankModel(
    type = type,
    costInUsd = costInUsd,
    costInRealTokens = costInRealTokens,
    image = pathToImage.let(ImageValue::Url),
    dailyReward = dailyReward,
    dailyUnlock = percentGrowingPerDay,
    dailyConsumption = dailyMaintenanceCostMultiplier ?: 0.0,
    isPurchasable = isAvailableToPurchase,
)

fun List<UserNftItemResponseDto>.toNftModelList() = map(UserNftItemResponseDto::toModel)

private fun UserNftItemResponseDto.toModel() = NftModel(
    id = id,
    userId = userId,
    tokenId = tokenId,
    isHealthy = isHealthy,
    mintedDate = requireNotNull(ZonedDateTime.parse(mintedDate)).toOffsetLocalDateTime(),
    type = data.type,
    costInUsd = data.costInUsd,
    costInRealTokens = data.costInRealTokens,
    image = data.pathToImage.let(ImageValue::Url),
    dailyReward = data.dailyReward,
    dailyUnlock = data.percentGrowingPerDay,
    dailyConsumption = data.dailyMaintenanceCostMultiplier ?: 0.0,
    isAvailableToPurchase = data.isAvailableToPurchase,
    repairCost = data.repairCost,
    isProcessing = isHealthy,
)