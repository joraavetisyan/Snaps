package io.snaps.featuretasks.data.model.instagram

import com.google.gson.annotations.SerializedName

data class UserResponseDto(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
)