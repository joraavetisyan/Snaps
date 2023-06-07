package io.snaps.baseprofile.data.model

import io.snaps.corecommon.model.DateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommonSettingsResponseDto(
    @SerialName("LikerGlassesReleaseDate") val likerGlassesReleaseDate: DateTime,
    @SerialName("MinimumLikerGlassesCount") val minimumLikerGlassesCount: Int,
    @SerialName("LikerSellsCount") val likerSellsCount: Int,
    @SerialName("ShowLiker") val showLiker: Boolean,
)