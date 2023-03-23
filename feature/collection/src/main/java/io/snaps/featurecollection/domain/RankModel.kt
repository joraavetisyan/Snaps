package io.snaps.featurecollection.domain

import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.model.NftType

data class RankModel(
    val type: NftType,
    val image: ImageValue,
    val dailyReward: Int,
    val dailyUnlock: Double,
    val dailyConsumption: Double,
    val isAvailableToPurchase: Boolean,
    val costInUsd: Int?,
    val costInRealTokens: Int?,
)