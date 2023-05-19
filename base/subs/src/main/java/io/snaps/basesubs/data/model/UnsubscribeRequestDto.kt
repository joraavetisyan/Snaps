package io.snaps.basesubs.data.model

import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnsubscribeRequestDto(
    @SerialName("userId") val subscriptionId: Uuid,
)