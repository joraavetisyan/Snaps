package io.snaps.coredata.network

import io.snaps.corecommon.model.Timestamp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    @SerialName("actualTimestamp") val actualTimestamp: Timestamp?,
    @SerialName("data") val data: T?,
    @SerialName("success") val isSuccess: Boolean = true,
) {

    val isSuccessAndHasData get() = isSuccess && data != null
}

@Serializable
data class ErrorResponse(
    @SerialName("actualTimestamp") val actualTimestamp: Timestamp?,
    @SerialName("error") val error: ErrorDto?,
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