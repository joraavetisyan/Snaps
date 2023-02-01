package io.snaps.basesession.data.model

import io.snaps.corecommon.model.DeviceId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LogoutRequestDto(
    @SerialName("deviceId") val deviceId: DeviceId,
)