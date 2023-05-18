package io.snaps.baseprofile.data.model

import io.snaps.corecommon.model.FullUrl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SocialPage(
    @SerialName("type") val type: String,
    @SerialName("link") val link: FullUrl,
)