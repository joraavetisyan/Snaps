package io.snaps.basenft.domain

import io.snaps.corecommon.model.BundleType
import io.snaps.corecommon.model.FiatValue
import io.snaps.corecommon.model.NftType

data class BundleModel(
    val type: BundleType,
    val fiatCost: FiatValue,
    val discountCost: FiatValue,
    val itemsInBundle: List<NftType>,
)