package io.snaps.basenft.domain

import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.model.CoinValue
import io.snaps.corecommon.model.FiatValue
import io.snaps.corecommon.model.NftType

data class RankModel(
    val type: NftType,
    val image: ImageValue,
    val cost: FiatValue?,
    val dailyReward: CoinValue,
    val dailyUnlock: Double,
    val dailyConsumption: Double,
    val isPurchasable: Boolean,
)