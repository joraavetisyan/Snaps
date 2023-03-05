package io.snaps.featuretasks.data

import io.snaps.corecommon.date.toOffsetLocalDateTime
import io.snaps.featuretasks.data.model.HistoryTaskItemResponseDto
import io.snaps.featuretasks.domain.TaskModel
import java.time.ZonedDateTime

fun List<HistoryTaskItemResponseDto>.toModelList() = map(HistoryTaskItemResponseDto::toTaskModel)

fun HistoryTaskItemResponseDto.toTaskModel() = TaskModel(
    id = id,
    userId = userId,
    date = requireNotNull(ZonedDateTime.parse(date)).toOffsetLocalDateTime(),
    energy = energy,
    experience = experience,
)