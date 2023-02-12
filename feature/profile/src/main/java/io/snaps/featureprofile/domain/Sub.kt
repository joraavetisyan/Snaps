package io.snaps.featureprofile.domain

import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.model.Uuid

data class Sub(
    val userId: Uuid,
    val image: ImageValue,
    val name: String,
    val isSubscribed: Boolean,
)