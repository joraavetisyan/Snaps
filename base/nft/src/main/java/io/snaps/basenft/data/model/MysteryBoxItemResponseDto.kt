package io.snaps.basenft.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MysteryBoxItemResponseDto(
    @SerialName("type") val type: MysteryBoxType,
    @SerialName("costInUsd") val costInUsd: Double,
    @SerialName("marketingProbabilities") val marketingProbabilities: ProbabilitiesDto,
    @SerialName("probabilities") val probabilities: ProbabilitiesDto,
)

@Serializable
data class ProbabilitiesDto(
    @SerialName("Follower") val follower: Double?,
    @SerialName("Sub") val sub: Double?,
    @SerialName("Sponsor") val sponsor: Double?,
    @SerialName("Influencer") val influencer: Double?,
    @SerialName("FamousGuy") val famousGuy: Double?,
    @SerialName("Rockstar") val rockstar: Double?,
    @SerialName("Star") val star: Double?,
    @SerialName("SuperStar") val superStar: Double?,
    @SerialName("Newbee") val newbie: Double?,
    @SerialName("Viewer") val viewer: Double?,
    @SerialName("Blogger") val blogger: Double?,
    @SerialName("Legend") val legend: Double?,
)

@Serializable
enum class MysteryBoxType {
    @SerialName("FirstTier") FirstTier,
    @SerialName("SecondTier") SecondTier,
}