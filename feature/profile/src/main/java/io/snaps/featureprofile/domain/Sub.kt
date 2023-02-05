package io.snaps.featureprofile.domain

import io.snaps.corecommon.container.ImageValue

data class Sub(
    val image: ImageValue,
    val name: String,
    val isSubscribed: Boolean,
)