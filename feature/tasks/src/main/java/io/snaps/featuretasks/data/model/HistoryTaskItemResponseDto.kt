package io.snaps.featuretasks.data.model

import io.snaps.corecommon.model.DateTime
import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class HistoryTaskItemResponseDto(
    @SerialName("id") val id: Uuid,
    @SerialName("userId") val userId: Uuid,
    @SerialName("date") val date: DateTime,
    @SerialName("experience") val experience: Int,
    @SerialName("energy") val energy: Int,
)