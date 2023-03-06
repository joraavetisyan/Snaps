package io.snaps.featurecollection.domain

import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.model.NftType

data class NftModel(
    val type: NftType,
    val price: Int,
    val image: ImageValue,
    val dailyReward: Int,
    val dailyUnlock: Double,
    val dailyConsumption: Double,
)