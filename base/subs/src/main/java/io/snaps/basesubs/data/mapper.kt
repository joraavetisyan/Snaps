package io.snaps.basesubs.data

import io.snaps.basesubs.data.model.SubsItemResponseDto
import io.snaps.basesubs.domain.SubModel
import io.snaps.corecommon.container.imageValue

fun List<SubsItemResponseDto>.toModelList(mySubscriptions: List<SubsItemResponseDto>?) = map { sub ->
    sub.toSubModel(
        isSubscribed = mySubscriptions?.let { list ->
            list.firstOrNull { it.userId == sub.userId } != null
        },
    )
}

fun SubsItemResponseDto.toSubModel(isSubscribed: Boolean?) = SubModel(
    entityId = entityId,
    userId = userId,
    avatar = avatar?.imageValue(),
    name = name.orEmpty(),
    isSubscribed = isSubscribed,
)