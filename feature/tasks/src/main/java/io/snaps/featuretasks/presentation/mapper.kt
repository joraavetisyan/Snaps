package io.snaps.featuretasks.presentation

import io.snaps.corecommon.model.NftModel
import io.snaps.basenft.ui.CollectionItemState
import io.snaps.basenft.ui.costToString
import io.snaps.basenft.ui.dailyRewardToString
import io.snaps.baseprofile.data.model.SocialPostStatus
import io.snaps.baseprofile.domain.QuestInfoModel
import io.snaps.baseprofile.domain.QuestModel
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.toPercentageFormat
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.QuestType
import io.snaps.corecommon.model.State
import io.snaps.corecommon.strings.StringKey
import io.snaps.featuretasks.presentation.ui.RemainingTimeTileState
import io.snaps.featuretasks.presentation.ui.TaskStatus
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
    status = status(),
    clickListener = { onItemClicked(this) },
)

fun State<QuestInfoModel>.toRemainingTimeTileState(
    remainingTime: Long,
) = when (this) {
    is Loading -> RemainingTimeTileState.Shimmer
    is Effect -> when {
        isSuccess -> RemainingTimeTileState.Data(
            time = remainingTime,
        )
        else -> RemainingTimeTileState.Shimmer
    }
}

fun QuestModel.energyProgress(): Int {
    return if (madeCount != null && count != null) {
        val madeByOne = madeCount!!.toDouble() / count!!.toDouble() * 20
        Integer.min(madeByOne.toInt(), energy)
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
    QuestType.Like -> StringKey.TasksMessageLike.textValue()
    QuestType.PublishVideo -> StringKey.TasksMessagePublishVideo.textValue()
    QuestType.Subscribe -> StringKey.TasksMessageSubscribe.textValue()
    QuestType.Watch -> StringKey.TasksMessageWatchVideo.textValue()
    else -> StringKey.TasksMessageSocialPost.textValue()
}

fun QuestModel.status(): TaskStatus? = when (status) {
    SocialPostStatus.Success -> TaskStatus.Credited
    SocialPostStatus.NotPosted -> TaskStatus.NotPosted
    SocialPostStatus.WaitForVerification -> TaskStatus.WaitForVerification
    SocialPostStatus.Rejected -> TaskStatus.Rejected
    null -> if (energyProgress() == energy) {
        TaskStatus.Credited
    } else if (energyProgress() > 0) {
        TaskStatus.InProgress
    } else null
    SocialPostStatus.NotSendToVerify -> TaskStatus.NotSendToVerify
}

fun State<List<NftModel>>.toNftCollectionItemState(
    onReloadClicked: () -> Unit,
    onItemClicked: (NftModel) -> Unit,
) = when (this) {
    is Loading -> List(6) { CollectionItemState.Shimmer }
    is Effect -> when {
        isSuccess -> buildList<CollectionItemState> {
            requireData.forEach {
                add(it.toNftCollectionItemState(onItemClicked))
            }
        }
        else -> listOf(CollectionItemState.Error(onClick = onReloadClicked))
    }
}

private fun NftModel.toNftCollectionItemState(
    onItemClicked: (NftModel) -> Unit,
) = CollectionItemState.Nft(
    type = type,
    price = costInUsd?.costToString() ?: "",
    image = image,
    dailyReward = dailyReward.dailyRewardToString(),
    dailyUnlock = dailyUnlock.toPercentageFormat(),
    dailyConsumption = dailyConsumption.toPercentageFormat(),
    isHealthy = true,
    isProcessing = false,
    onRepairClicked = {},
    onItemClicked = { onItemClicked(this) },
    onProcessingClicked = {},
)