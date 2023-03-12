package io.snaps.featurecollection.data

import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.mock.rBool
import io.snaps.corecommon.mock.rDouble
import io.snaps.corecommon.mock.rImage
import io.snaps.corecommon.mock.rInt
import io.snaps.featurecollection.data.model.NftItemResponseDto
import io.snaps.featurecollection.data.model.UserNftItemResponseDto
import io.snaps.featurecollection.domain.NftModel
import io.snaps.featurecollection.domain.RankModel

fun List<NftItemResponseDto>.toRankModelList() = map(NftItemResponseDto::toModel)

private fun NftItemResponseDto.toModel() = RankModel(
    type = type,
    price = rInt, // todo absent in api model
    image = ImageValue.Url(rImage), // todo absent in api model
    dailyReward = dailyReward,
    dailyUnlock = dailyUnlock,
    dailyConsumption = dailyConsumption,
    isSelected = false, // todo
)

fun List<UserNftItemResponseDto>.toNftModelList() = map(UserNftItemResponseDto::toModel)

private fun UserNftItemResponseDto.toModel() = NftModel(
    type = type,
    price = rInt, // todo
    image = ImageValue.Url(rImage), // todo
    dailyConsumption = rDouble, // todo
    dailyUnlock = rDouble, // todo
    dailyReward = rInt, // todo
)