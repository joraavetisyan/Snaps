package io.snaps.basesources.remotedata.model

import io.snaps.corecommon.model.FullUrl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppUpdateInfoDto(
    @SerialName("version") val versionCode: Int,
    @SerialName("link") val link: FullUrl,
)