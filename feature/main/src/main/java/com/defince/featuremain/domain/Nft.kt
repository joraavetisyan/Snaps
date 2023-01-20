package com.defince.featuremain.domain

import com.defince.corecommon.container.ImageValue

data class Nft(
    val image: ImageValue,
    val reward: String,
    val dailyUnlock: String,
    val dailyCosts: String,
)