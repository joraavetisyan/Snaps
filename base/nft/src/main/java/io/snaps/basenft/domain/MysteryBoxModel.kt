package io.snaps.basenft.domain

import io.snaps.corecommon.model.FiatValue
import io.snaps.corecommon.model.MysteryBoxType
import io.snaps.corecommon.model.NftType

data class MysteryBoxModel(
    val type: MysteryBoxType,
    val fiatCost: FiatValue,
    val marketingProbabilities: List<ProbabilityModel>,
)

data class ProbabilityModel(
    val nftType: NftType,
    val probability: Double?,
)