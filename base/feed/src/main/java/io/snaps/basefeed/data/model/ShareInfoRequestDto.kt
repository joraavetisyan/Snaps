package io.snaps.basefeed.data.model

import io.snaps.corecommon.model.SocialNetwork
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShareInfoRequestDto(
    @SerialName("network") val network: SocialNetwork,
)