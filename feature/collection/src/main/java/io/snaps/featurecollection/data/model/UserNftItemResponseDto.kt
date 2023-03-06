package io.snaps.featurecollection.data.model

import io.snaps.corecommon.model.DateTime
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserNftItemResponseDto(
    @SerialName("id") val id: Uuid,
    @SerialName("userId") val userId: Uuid,
    @SerialName("nftGoogleType") val type: NftType,
    @SerialName("mintedDate") val mintedDate: DateTime,
)