package io.snaps.basefeed.data.model

import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddVideoRequestDto(
    @SerialName("title") val title: String,
    /*@SerialName("description") val description: String,*/
    @SerialName("thumbnailFileId") val thumbnailFileId: Uuid,
)