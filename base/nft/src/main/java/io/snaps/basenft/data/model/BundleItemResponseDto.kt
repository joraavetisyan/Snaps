package io.snaps.basenft.data.model

import io.snaps.corecommon.model.BundleType
import io.snaps.corecommon.model.NftType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BundleItemResponseDto(
    @SerialName("type") val type: BundleType,
    @SerialName("costInUsd") val costInUsd: Double,
    @SerialName("discount") val discount: Double,
    @SerialName("itemsInBundle") val itemsInBundle: List<NftType>,
)