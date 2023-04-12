package io.snaps.baseprofile.data.model

import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.model.WalletAddress
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ConnectInstagramRequestDto(
    @SerialName("instagramId") val instagramId: Uuid?,
    @SerialName("name") val name: String,
    @SerialName("avatar") val avatarUrl: FullUrl,
    @SerialName("wallet") val wallet: WalletAddress,
)