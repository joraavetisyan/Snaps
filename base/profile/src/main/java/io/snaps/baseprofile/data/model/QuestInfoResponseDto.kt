package io.snaps.baseprofile.data.model

import io.snaps.corecommon.model.DateTime
import io.snaps.corecommon.model.QuestType
import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestInfoResponseDto(
    @SerialName("quests") val quests: List<QuestItemDto>,
    @SerialName("questDate") val questDate: DateTime,
    @SerialName("roundEndDate") val roundEndDate: DateTime,
    @SerialName("experience") val experience: Int,
    @SerialName("energy") val energy: Int,
    @SerialName("roundId") val roundId: Uuid,
    @SerialName("id") val id: Uuid,
)

@Serializable
data class QuestItemDto(
    @SerialName("completed") val completed: Boolean,
    @SerialName("quest") val quest: QuestDto,
    @SerialName("madeCount") val madeCount: Int?,
    @SerialName("done") val done: Boolean?,

    // For SocialPost
    @SerialName("status") val status: SocialPostStatus?,
)

@Serializable
data class QuestDto(
    @SerialName("count") val count: Int?,
    @SerialName("type") val type: QuestType,
    @SerialName("energy") val energy: Int,
)

@Serializable
enum class SocialPostStatus {
    NotPosted,
    WaitForVerification,
    Success,
    Rejected,
    NotSendToVerify,
}