package io.snaps.basesubs.data.model

import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubscribeRequestDto(
    @SerialName("toSubscribeUserId") val toSubscribeUserId: Uuid,
)