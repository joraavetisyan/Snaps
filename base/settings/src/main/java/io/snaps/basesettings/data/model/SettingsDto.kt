package io.snaps.basesettings.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SettingsDto(
    @SerialName("android_buy_button") val purchaseNftInStore: Boolean,
    @SerialName("android_toggles") val togglesVersion: Int,
    @SerialName("android_version") val appUpdateInfo: AppUpdateInfoDto,
    @SerialName("captcha_enabled") val captcha: Boolean,
    @SerialName("max_videos_count") val maxVideosCount: Int,
    @SerialName("mobile_banner") val banner: BannerDto,
    @SerialName("nft_bnb_enabled") val purchaseNftWithBnb: Boolean,
    @SerialName("snaps_cell_enabled") val sellSnaps: Boolean,
    @SerialName("social") val socialPages: List<SocialPageDto>,
    @SerialName("video_key") val videoKey: String,
    @SerialName("video_key_dev") val videoKeyDev: String,
    @SerialName("mobile_add") val ad: AdDto?,
    @SerialName("mystery_enabled") val mysteryBox: Boolean,
    @SerialName("bundle_enabled") val bundle: Boolean,
)