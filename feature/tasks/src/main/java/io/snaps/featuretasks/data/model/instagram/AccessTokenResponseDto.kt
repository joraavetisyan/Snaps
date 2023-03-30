package io.snaps.featuretasks.data.model.instagram

import com.google.gson.annotations.SerializedName

data class AccessTokenResponseDto(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("user_id") val userId: String,
)