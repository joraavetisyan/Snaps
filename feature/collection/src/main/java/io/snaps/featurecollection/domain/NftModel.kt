package io.snaps.featurecollection.domain

import io.snaps.corecommon.container.ImageValue

data class NftModel(
    val items: List<NftItem>,
    val maxCount: Int,
)

data class NftItem(
    val type: String,
    val price: String,
    val image: ImageValue,
    val dailyReward: String,
    val dailyUnlock: String,
    val dailyConsumption: String,
)