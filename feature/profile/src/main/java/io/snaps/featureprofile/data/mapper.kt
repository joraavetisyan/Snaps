package io.snaps.featureprofile.data

import io.snaps.corecommon.container.ImageValue
import io.snaps.featureprofile.data.model.SubscriptionItemResponseDto
import io.snaps.featureprofile.domain.Sub

fun List<SubscriptionItemResponseDto>.toModelList() = map(SubscriptionItemResponseDto::toSubModel)

fun SubscriptionItemResponseDto.toSubModel() = Sub(
    userId = userId,
    image = ImageValue.Url(imageUrl),
    name = name,
    isSubscribed = isSubscribed,
)