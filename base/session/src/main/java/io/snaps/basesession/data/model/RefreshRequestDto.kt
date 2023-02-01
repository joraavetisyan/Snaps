package io.snaps.basesession.data.model

import io.snaps.corecommon.model.DateTime
import io.snaps.corecommon.model.DeviceId
import io.snaps.corecommon.model.Token
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequestDto(
    @SerialName("refreshToken") val refreshToken: Token,
    @SerialName("deviceId") val deviceId: DeviceId,
    @SerialName("deviceName") val deviceName: String,
    @SerialName("requestDatetime") val requestDatetime: DateTime,
)