package io.snaps.baseprofile.data.model

import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.CryptoAddress
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class EditUserRequestDto(
    @SerialName("name") val name: String,
    @SerialName("avatar") val avatarUrl: FullUrl?,
    @SerialName("wallet") val wallet: CryptoAddress,
)