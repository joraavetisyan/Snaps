package io.snaps.basenft.data

import io.snaps.basenft.data.model.NftItemResponseDto
import io.snaps.basenft.data.model.UserNftItemResponseDto
import io.snaps.basenft.domain.RankModel
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.date.toOffsetLocalDateTime
import io.snaps.corecommon.model.CoinSNPS
import io.snaps.corecommon.model.FiatUSD
import io.snaps.basenft.domain.NftModel
import java.time.ZonedDateTime

fun List<NftItemResponseDto>.toRankModelList() = map(NftItemResponseDto::toModel)

private fun NftItemResponseDto.toModel() = RankModel(
    type = type,
    cost = costInUsd?.let { FiatUSD(it.toDouble()) },
    image = pathToImage.imageValue(),
    dailyReward = CoinSNPS(dailyReward.toDouble()),
    dailyUnlock = percentGrowingPerDay,
    dailyConsumption = dailyMaintenanceCostMultiplier ?: 0.0,
    isPurchasable = isAvailableToPurchase,
    additionalData = toAdditionalData(),
)

private fun NftItemResponseDto.toAdditionalData() = RankModel.AdditionalData(
    leftCopies = additionalData?.leftCopies,
)

fun List<UserNftItemResponseDto>.toNftModelList() = map(UserNftItemResponseDto::toModel)

private fun UserNftItemResponseDto.toModel() = NftModel(
    id = id,
    userId = userId,
    tokenId = tokenId,
    isHealthy = isHealthy,
    mintDate = requireNotNull(ZonedDateTime.parse(mintedDate)).toOffsetLocalDateTime(),
    type = data.type,
    fiatCost = data.costInUsd?.let { FiatUSD(it.toDouble()) },
    image = data.pathToImage.imageValue(),
    dailyReward = CoinSNPS(data.dailyReward.toDouble()),
    dailyUnlock = data.percentGrowingPerDay,
    dailyConsumption = data.dailyMaintenanceCostMultiplier ?: 0.0,
    isPurchasable = data.isAvailableToPurchase,
    repairCost = CoinSNPS(data.repairCost),
    level = levelInfo.level,
    lowerThreshold = levelInfo.lowerThreshold,
    upperThreshold = levelInfo.upperThreshold,
    bonus = levelInfo.bonus,
    experience = levelInfo.experience,
)