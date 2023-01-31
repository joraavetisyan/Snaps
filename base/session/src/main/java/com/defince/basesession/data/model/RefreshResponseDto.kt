package com.defince.basesession.data.model

import com.defince.corecommon.model.Token
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshResponseDto(
    @SerialName("refreshToken") val refreshToken: Token,
    @SerialName("accessToken") val accessToken: Token,
)