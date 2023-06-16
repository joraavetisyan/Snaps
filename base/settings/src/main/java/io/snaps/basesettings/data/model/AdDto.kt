package io.snaps.basesettings.data.model

import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdDto(
    @SerialName("show_place") val showPlace: Int,
    @SerialName("is_shown") val isShown: Boolean,
    @SerialName("video_link") val videoUrl: FullUrl,
    @SerialName("open_url") val openUrl: FullUrl,
    @SerialName("title") val title: String,
    @SerialName("username") val username: String,
    @SerialName("entityId") val entityId: Uuid,
    @SerialName("avatar") val avatar: FullUrl,
)