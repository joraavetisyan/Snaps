package io.snaps.basesubs.data

import io.snaps.basesubs.data.model.SubsItemResponseDto
import io.snaps.basesubs.domain.SubModel
import io.snaps.corecommon.container.ImageValue

fun List<SubsItemResponseDto>.toModelList(mySubscriptions: List<SubsItemResponseDto>?) = map { sub ->
    sub.toSubModel(
        mySubscriptions?.let { list ->
            list.firstOrNull { it.entityId == sub.entityId } != null
        }
    )
}

fun SubsItemResponseDto.toSubModel(isSubscribed: Boolean?) = SubModel(
    entityId = entityId,
    userId = userId,
    avatar = avatar?.let(ImageValue::Url),
    name = name.orEmpty(),
    isSubscribed = isSubscribed,
)