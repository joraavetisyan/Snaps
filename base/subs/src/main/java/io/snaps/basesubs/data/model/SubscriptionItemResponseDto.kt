package io.snaps.basesubs.data.model

import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SubscriptionItemResponseDto(
    @SerialName("userId") val userId: Uuid,
    @SerialName("imageUrl") val imageUrl: FullUrl,
    @SerialName("name") val name: String,
    @SerialName("isSubscribed") val isSubscribed: Boolean,
)