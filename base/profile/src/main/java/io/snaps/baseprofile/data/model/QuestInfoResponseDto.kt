package io.snaps.baseprofile.data.model

import io.snaps.corecommon.model.DateTime
import io.snaps.corecommon.model.QuestType
import io.snaps.corecommon.model.SocialNetwork
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestInfoResponseDto(
    @SerialName("quests") val quests: List<QuestItemDto>,
    @SerialName("questDate") val questDate: DateTime,
    @SerialName("updatedDate") val updatedDate: DateTime,
    @SerialName("experience") val experience: Int,
    @SerialName("energy") val energy: Int,
)

@Serializable
data class QuestItemDto(
    @SerialName("energyProgress") val energyProgress: Int,
    @SerialName("completed") val completed: Boolean,
    @SerialName("quest") val quest: QuestDto,
    @SerialName("madeCount") val madeCount: Int?,
    @SerialName("done") val done: Boolean?,

    // For SocialPost
    @SerialName("network") val network: SocialNetwork?,
)

@Serializable
data class QuestDto(
    @SerialName("count") val count: Int?,
    @SerialName("type") val type: QuestType,
    @SerialName("energy") val energy: Int,
)