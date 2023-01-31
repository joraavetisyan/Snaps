package com.defince.basesession.data.model

import com.defince.corecommon.model.DateTime
import com.defince.corecommon.model.DeviceId
import com.defince.corecommon.model.Token
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequestDto(
    @SerialName("refreshToken") val refreshToken: Token,
    @SerialName("deviceId") val deviceId: DeviceId,
    @SerialName("deviceName") val deviceName: String,
    @SerialName("requestDatetime") val requestDatetime: DateTime,
)