package io.snaps.basefeed.data.model

import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddVideoResponseDto(
    @SerialName("internalId") val internalId: Uuid,
)