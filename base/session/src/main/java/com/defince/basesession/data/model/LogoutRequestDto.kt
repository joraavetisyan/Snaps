package com.defince.basesession.data.model

import com.defince.corecommon.model.DeviceId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LogoutRequestDto(
    @SerialName("deviceId") val deviceId: DeviceId,
)