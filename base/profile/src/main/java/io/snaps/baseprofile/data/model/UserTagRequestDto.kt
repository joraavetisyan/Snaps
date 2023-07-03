package io.snaps.baseprofile.data.model

import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class UserTagRequestDto(
    @SerialName("tagIds") val tagIds: List<Uuid>,
)