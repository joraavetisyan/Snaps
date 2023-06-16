package io.snaps.basesettings.data

import io.snaps.basesettings.data.model.CommonSettingsResponseDto
import io.snaps.basesettings.domain.CommonSettingsModel
import io.snaps.corecommon.date.toOffsetLocalDateTime
import java.time.ZonedDateTime

fun CommonSettingsResponseDto.toModel() = CommonSettingsModel(
    likerGlassesReleaseDate = requireNotNull(ZonedDateTime.parse(likerGlassesReleaseDate)).toOffsetLocalDateTime(),
)