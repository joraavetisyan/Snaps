package io.snaps.featuremain.domain

import io.snaps.corecommon.container.ImageValue

data class Nft(
    val image: ImageValue,
    val reward: String,
    val dailyUnlock: String,
    val dailyCosts: String,
)