package io.snaps.basefile.data.model

import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadFileResponseDto(
    @SerialName("fileName") val fileName: String,
    @SerialName("fileId") val fileId: Uuid,
)