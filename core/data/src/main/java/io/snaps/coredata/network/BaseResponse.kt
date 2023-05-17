package io.snaps.coredata.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    @SerialName("data") val data: T?,
    @SerialName("success") val isSuccess: Boolean? = null,
)

@Serializable
data class ErrorResponse(
    @SerialName("error") val error: String?,
)

@Serializable
data class ErrorDto(
    @SerialName("code") val code: String?,
    @SerialName("message") val message: String?,
    @SerialName("displayMessage") val displayMessage: String?,
)

@Serializable
data class KeyValueDto(
    @SerialName("name") val name: String,
    @SerialName("value") val value: String,
)