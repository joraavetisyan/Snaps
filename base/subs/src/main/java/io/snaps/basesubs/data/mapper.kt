package io.snaps.basesubs.data

import io.snaps.corecommon.container.ImageValue
import io.snaps.basesubs.data.model.SubscriptionItemResponseDto
import io.snaps.basesubs.domain.SubModel

fun List<SubscriptionItemResponseDto>.toModelList() = map(SubscriptionItemResponseDto::toSubModel)

fun SubscriptionItemResponseDto.toSubModel() = SubModel(
    userId = userId,
    image = ImageValue.Url(imageUrl),
    name = name,
    isSubscribed = isSubscribed,
)