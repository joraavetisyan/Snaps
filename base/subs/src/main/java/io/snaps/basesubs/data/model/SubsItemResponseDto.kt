package io.snaps.basesubs.data.model

import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SubsItemResponseDto(
    @SerialName("entityId") val entityId: Uuid,
    @SerialName("userId") val userId: Uuid,
    @SerialName("avatar") val avatar: FullUrl?,
    @SerialName("name") val name: String?,
)