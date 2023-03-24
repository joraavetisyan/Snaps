package io.snaps.featuretasks.presentation

import io.snaps.baseprofile.domain.QuestInfoModel
import io.snaps.baseprofile.domain.QuestModel
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.date.toLong
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.QuestType
import io.snaps.corecommon.model.State
import io.snaps.corecommon.strings.StringKey
import io.snaps.featuretasks.presentation.ui.RemainingTimeTileState
import io.snaps.featuretasks.presentation.ui.TaskTileState

fun State<QuestInfoModel>.toTaskTileState(
    onItemClicked: (QuestModel) -> Unit,
    onReloadClicked: () -> Unit,
) = when (this) {
    is Loading -> List(6) { TaskTileState.Shimmer }
    is Effect -> when {
        isSuccess -> requireData.quests.map {
            it.toTaskTileState(onItemClicked = onItemClicked)
        }
        else -> listOf(TaskTileState.Error(clickListener = onReloadClicked))
    }
}

private fun QuestModel.toTaskTileState(
    onItemClicked: (QuestModel) -> Unit,
) = TaskTileState.Data(
    energy = energy,
    energyProgress = this.energyProgress(),
    title = type.toTaskTitle(),
    description = type.toTaskDescription(),
    clickListener = { onItemClicked(this) },
)

fun State<QuestInfoModel>.toRemainingTimeTileState() = when (this) {
    is Loading -> RemainingTimeTileState.Shimmer
    is Effect -> when {
        isSuccess -> RemainingTimeTileState.Data(
            time = requireData.questDate.plusHours(24).toLong(),
            energy = requireData.totalEnergy,
            energyProgress = requireData.totalEnergyProgress,
        )
        else -> RemainingTimeTileState.Shimmer
    }
}

fun QuestModel.energyProgress(): Int {
    return if (madeCount != null && count != null) {
        madeCount!! / count!! * 20
    } else {
        if (completed) {
            energy
        } else 0
    }
}

fun QuestType.toTaskTitle() = when (this) {
    QuestType.Like -> StringKey.TasksTitleLike.textValue()
    QuestType.PublishVideo -> StringKey.TasksTitlePublishVideo.textValue()
    QuestType.Subscribe -> StringKey.TasksTitleSubscribe.textValue()
    QuestType.Watch -> StringKey.TasksTitleWatchVideo.textValue()
    else -> StringKey.TasksTitleSocialPost.textValue()
}

fun QuestType.toTaskDescription() = when (this) {
    QuestType.Like -> StringKey.TasksDescriptionLike.textValue()
    QuestType.PublishVideo -> StringKey.TasksDescriptionPublishVideo.textValue()
    QuestType.Subscribe -> StringKey.TasksDescriptionSubscribe.textValue()
    QuestType.Watch -> StringKey.TasksDescriptionWatchVideo.textValue()
    else -> StringKey.TasksDescriptionSocialPost.textValue()
}