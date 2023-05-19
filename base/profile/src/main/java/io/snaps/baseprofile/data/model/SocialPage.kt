package io.snaps.baseprofile.data.model

import io.snaps.corecommon.model.FullUrl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SocialPage(
    @SerialName("type") val type: SocialPageType = SocialPageType.Unknown,
    @SerialName("link") val link: FullUrl?,
)

@Serializable
enum class SocialPageType {
    @SerialName("discord") Discord,
    @SerialName("twitter") Twitter,
    @SerialName("telegram") Telegram,
    @SerialName("instagram") Instagram,
    @SerialName("support") Support,
    Unknown,
}