package com.defince.featuremain.domain

import com.defince.corecommon.container.ImageValue

data class Rank(
    val type: String,
    val price: String,
    val image: ImageValue,
    val dailyReward: String,
    val dailyUnlock: String,
    val dailyConsumption: String,
    val dosagePerDayMonth: String,
    val spendingOnGas: String,
)