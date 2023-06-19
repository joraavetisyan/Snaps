package io.snaps.basenft.data

import io.snaps.basenft.data.model.MysteryBoxItemResponseDto
import io.snaps.basenft.data.model.NftItemResponseDto
import io.snaps.basenft.data.model.ProbabilitiesDto
import io.snaps.basenft.data.model.UserNftItemResponseDto
import io.snaps.basenft.domain.MysteryBoxModel
import io.snaps.basenft.domain.RankModel
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.date.toOffsetLocalDateTime
import io.snaps.corecommon.model.CoinSNPS
import io.snaps.corecommon.model.FiatUSD
import io.snaps.basenft.domain.NftModel
import io.snaps.basenft.domain.ProbabilitiesModel
import java.time.ZonedDateTime

fun List<NftItemResponseDto>.toRankModelList() = map(NftItemResponseDto::toModel)

private fun NftItemResponseDto.toModel() = RankModel(
    type = type,
    cost = costInUsd?.let { FiatUSD(it) },
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
    fiatCost = data.costInUsd?.let { FiatUSD(it) },
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

fun List<MysteryBoxItemResponseDto>.toMysteryBoxModelList() = map(MysteryBoxItemResponseDto::toModel)

private fun MysteryBoxItemResponseDto.toModel() = MysteryBoxModel(
    type = type,
    fiatCost = FiatUSD(costInUsd),
    probabilities = probabilities.toModel(),
    marketingProbabilities = marketingProbabilities.toModel(),
)

private fun ProbabilitiesDto.toModel() = ProbabilitiesModel(
    follower = follower,
    sub = sub,
    sponsor = sponsor,
    influencer = influencer,
    famousGuy = famousGuy,
    rockstar = rockstar,
    star = star,
    superStar = superStar,
    newbie = newbie,
    viewer = viewer,
    blogger = blogger,
    legend = legend,
)