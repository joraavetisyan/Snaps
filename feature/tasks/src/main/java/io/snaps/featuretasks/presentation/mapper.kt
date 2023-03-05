package io.snaps.featuretasks.presentation

import io.snaps.baseprofile.data.model.QuestType
import io.snaps.baseprofile.domain.QuestInfoModel
import io.snaps.baseprofile.domain.QuestModel
import io.snaps.corecommon.date.toLong
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.featuretasks.presentation.ui.TaskTileState

fun State<QuestInfoModel>.toTaskTileState(
    onItemClicked: (QuestModel) -> Unit,
    onReloadClicked: () -> Unit,
) = when (this) {
    is Loading -> List(6) { TaskTileState.Shimmer }
    is Effect -> when {
        isSuccess -> buildList<TaskTileState> {
            add(
                TaskTileState.RemainingTime(
                    time = requireData.questDate.toLong(),
                    energy = requireData.totalEnergy,
                    energyProgress = requireData.totalEnergyProgress,
                )
            )
            requireData.quests.forEach {
                add(it.toTaskTileState(onItemClicked = onItemClicked))
            }
        }
        else -> listOf(TaskTileState.Error(clickListener = onReloadClicked))
    }
}

private fun QuestModel.toTaskTileState(
    onItemClicked: (QuestModel) -> Unit,
) = TaskTileState.Data(
    energy = energy,
    energyProgress = energyProgress,
    done = completed,
    title = when (type) { // todo
        QuestType.Like -> "Like"
        QuestType.PublishVideo -> "Publish video"
        QuestType.SocialPost -> "Social post"
        QuestType.Subscribe -> "Subscribe"
        QuestType.SocialShare -> "Social share"
        QuestType.Watch -> "Watch video"
    },
    description = when (type) { // todo
        QuestType.Like -> "At least 10 likes, at least 5 subscriptions = 10 energy points"
        QuestType.PublishVideo -> "The minimum video length is 5 seconds, the maximum video length is 1 minute = 15 energy points."
        QuestType.SocialPost -> "View at least 50 videos with a retention of at least 70 percent"
        QuestType.Subscribe -> "At least 10 likes, at least 5 subscriptions = 10 energy points"
        QuestType.SocialShare -> ""
        QuestType.Watch -> "Watch at least 50 videos with a retention of at least 70% to get 15 energy points."
    },
    clickListener = { onItemClicked(this) },
)