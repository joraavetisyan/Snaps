package io.snaps.featuretasks.data.model

import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TaskItemResponseDto(
    @SerialName("id") val id: Uuid,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("energy") val energy: Int,
    @SerialName("energyProgress") val energyProgress: Int,
    @SerialName("type") val type: TaskType,
    @SerialName("done") val done: Boolean,

    // For LikeAndSubscribe, WatchVideo
    @SerialName("count") val count: Int?,
    @SerialName("madeCount") val madeCount: Int?,
)

@Serializable
enum class TaskType {
    WatchVideo,
    LikeAndSubscribe,
    Share,
    FindPoints,
}