package io.snaps.basenft.domain

import io.snaps.basenft.data.model.MysteryBoxType
import io.snaps.corecommon.model.FiatValue
import kotlinx.serialization.Serializable

data class MysteryBoxModel(
    val type: MysteryBoxType,
    val fiatCost: FiatValue,
    val probabilities: ProbabilitiesModel,
    val marketingProbabilities: ProbabilitiesModel,
)

@Serializable
data class ProbabilitiesModel(
    val follower: Double?,
    val sub: Double?,
    val sponsor: Double?,
    val influencer: Double?,
    val famousGuy: Double?,
    val rockstar: Double?,
    val star: Double?,
    val superStar: Double?,
    val newbie: Double?,
    val viewer: Double?,
    val blogger: Double?,
    val legend: Double?,
)