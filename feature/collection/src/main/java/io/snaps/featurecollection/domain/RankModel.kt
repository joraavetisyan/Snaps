package io.snaps.featurecollection.domain

import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.model.Uuid

data class RankModel(
    val id: Uuid,
    val type: String,
    val price: String,
    val image: ImageValue,
    val dailyReward: String,
    val dailyUnlock: String,
    val dailyConsumption: String,
    val isSelected: Boolean,
)