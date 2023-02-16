package io.snaps.featureprofile.data.model

import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnsubscribeRequestDto(
    @SerialName("subscriptionId") val subscriptionId: Uuid,
)