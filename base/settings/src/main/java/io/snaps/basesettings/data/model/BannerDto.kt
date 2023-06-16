package io.snaps.basesettings.data.model

import io.snaps.corecommon.model.FullUrl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BannerDto(
    @SerialName("isShown") val isViewable: Boolean,
    @SerialName("endlessBanner") val isEndless: Boolean,
    @SerialName("version") val version: Int,
    @SerialName("image") val image: FullUrl,
    @SerialName("action") val action: FullUrl,
    @SerialName("actionTitle") val actionTitle: BannerTitleDto,
    @SerialName("title") val title: BannerTitleDto,
    @SerialName("actionType") val actionType: BannerActionType?,
    @SerialName("showTimer") val isTimerShown: Boolean,
)

@Serializable
data class BannerTitleDto(
    @SerialName("en") val en: String,
    @SerialName("ru_RU") val ru: String,
    @SerialName("tr") val tr: String,
    @SerialName("uk") val uk: String,
    @SerialName("es") val es: String,
)

@Serializable
enum class BannerActionType {
    @SerialName("nft_list") NftList,
}