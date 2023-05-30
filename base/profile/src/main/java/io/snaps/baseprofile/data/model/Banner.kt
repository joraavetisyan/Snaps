package io.snaps.baseprofile.data.model

import io.snaps.corecommon.model.FullUrl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Banner(
    @SerialName("isShown") val isShown: Boolean,
    @SerialName("version") val version: Int,
    @SerialName("image") val image: FullUrl,
    @SerialName("action") val action: FullUrl,
    @SerialName("actionTitle") val actionTitle: Title,
    @SerialName("title") val title: Title,
)

@Serializable
data class Title(
    @SerialName("en") val en: String,
    @SerialName("ru_RU") val ru: String,
    @SerialName("tr") val tr: String,
    @SerialName("uk") val uk: String,
    @SerialName("es") val es: String,
)