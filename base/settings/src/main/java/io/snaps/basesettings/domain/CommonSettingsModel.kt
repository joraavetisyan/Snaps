package io.snaps.basesettings.domain

import java.time.LocalDateTime

data class CommonSettingsModel(
    val likerGlassesReleaseDate: LocalDateTime,
    val minimumLikerGlassesCount: Int,
    val likerSellsCount: Int,
    val showLiker: Boolean,
)