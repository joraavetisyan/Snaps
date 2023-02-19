package io.snaps.featurecollection.data

import io.snaps.corecommon.container.ImageValue
import io.snaps.featurecollection.data.model.NftItemDto
import io.snaps.featurecollection.data.model.NftResponseDto
import io.snaps.featurecollection.data.model.RankItemResponseDto
import io.snaps.featurecollection.domain.NftItem
import io.snaps.featurecollection.domain.NftModel
import io.snaps.featurecollection.domain.RankModel

fun List<RankItemResponseDto>.toModelList() = map(RankItemResponseDto::toModel)

fun RankItemResponseDto.toModel() = RankModel(
    id = id,
    type = type,
    price = price,
    image = ImageValue.Url(image),
    dailyReward = dailyReward,
    dailyUnlock = dailyUnlock,
    dailyConsumption = dailyConsumption,
    isSelected = isSelected,
)

fun NftResponseDto.toModel() = NftModel(
    items = items.map(NftItemDto::toModel),
    maxCount = maxCount,
)

fun NftItemDto.toModel() = NftItem(
    type = type,
    price = price,
    image = ImageValue.Url(image),
    dailyReward = dailyReward,
    dailyUnlock = dailyUnlock,
    dailyConsumption = dailyConsumption,
)