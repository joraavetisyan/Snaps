package io.snaps.basenft.domain

import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.model.NftType

data class RankModel(
    val type: NftType,
    val image: ImageValue,
    val dailyReward: Int,
    val dailyUnlock: Double,
    val dailyConsumption: Double,
    val isPurchasable: Boolean,
    val costInUsd: Int?,
    val costInRealTokens: Int?,
)