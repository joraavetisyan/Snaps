package io.snaps.featuremain.domain

import io.snaps.corecommon.container.ImageValue

data class Sub(
    val image: ImageValue,
    val name: String,
    val isSubscribed: Boolean,
)