package io.snaps.featuretasks.presentation

import io.snaps.basenft.domain.NftModel
import io.snaps.basenft.ui.CollectionItemState
import io.snaps.baseprofile.data.model.SocialPostStatus
import io.snaps.baseprofile.domain.QuestInfoModel
import io.snaps.baseprofile.domain.QuestModel
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.toPercentageFormat
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.TaskType
import io.snaps.corecommon.model.State
import io.snaps.corecommon.strings.StringKey
import io.snaps.featuretasks.presentation.ui.RemainingTimeTileState
import io.snaps.featuretasks.presentation.ui.TaskStatus
import io.snaps.featuretasks.presentation.ui.TaskTileState
import kotlin.time.Duration

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
    title = type.toTaskTitle(count),
    description = type.toTaskDescription(count),
    status = status(),
    clickListener = { onItemClicked(this) },
)

fun taskDefaultCount(type: TaskType) = when (type) {
    TaskType.Like -> 10
    TaskType.PublishVideo -> -1
    TaskType.Subscribe -> 5
    TaskType.Watch -> 20
    TaskType.SocialShare -> -1
    TaskType.SocialPost -> -1
}

fun State<QuestInfoModel>.toRemainingTimeTileState(
    remainingTime: Duration,
) = when (this) {
    is Loading -> RemainingTimeTileState.Shimmer
    is Effect -> when {
        isSuccess -> RemainingTimeTileState.Data(
            time = remainingTime,
        )
        else -> RemainingTimeTileState.Shimmer
    }
}

// todo must be on back
fun QuestModel.energyProgress(): Int {
    return if (madeCount != null && count != null) {
        val madeByOne = madeCount!!.toDouble() / count!!.toDouble() * energy
        madeByOne.toInt()
    } else {
        if (completed) {
            energy
        } else 0
    }
}

fun TaskType.toTaskTitle(count: Int?) = when (this) {
    TaskType.Like -> StringKey.TasksTitleLike.textValue()
    TaskType.PublishVideo -> StringKey.TasksTitlePublishVideo.textValue()
    TaskType.Subscribe -> StringKey.TasksTitleSubscribe.textValue()
    TaskType.Watch -> StringKey.TasksTitleWatchVideo.textValue((count ?: taskDefaultCount(this)).toString())
    else -> StringKey.TasksTitleSocialPost.textValue()
}

fun TaskType.toTaskDescription(count: Int?) = when (this) {
    TaskType.Like -> StringKey.TasksMessageLike.textValue()
    TaskType.PublishVideo -> StringKey.TasksMessagePublishVideo.textValue()
    TaskType.Subscribe -> StringKey.TasksMessageSubscribe.textValue()
    TaskType.Watch -> StringKey.TasksMessageWatchVideo.textValue((count ?: taskDefaultCount(this)).toString())
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
    snpsUsdExchangeRate: Double,
    onReloadClicked: () -> Unit,
    onItemClicked: (NftModel) -> Unit,
) = when (this) {
    is Loading -> List(6) { CollectionItemState.Shimmer }
    is Effect -> when {
        isSuccess -> requireData.map {
            it.toNftCollectionItemState(
                snpsUsdExchangeRate = snpsUsdExchangeRate,
                onItemClicked = onItemClicked
            )
        }
        else -> listOf(CollectionItemState.Error(onClick = onReloadClicked))
    }
}

private fun NftModel.toNftCollectionItemState(
    snpsUsdExchangeRate: Double,
    onItemClicked: (NftModel) -> Unit,
) = CollectionItemState.Nft(
    type = type,
    image = image,
    dailyReward = dailyReward.toFiat(rate = snpsUsdExchangeRate),
    dailyUnlock = dailyUnlock.toPercentageFormat(),
    dailyConsumption = dailyConsumption.toPercentageFormat(),
    isHealthBadgeVisible = !isHealthy,
    isRepairable = false, // repair is not displayed on the tasks screen
    level = level,
    experience = experience,
    bonus = bonus,
    upperThreshold = upperThreshold,
    lowerThreshold = lowerThreshold,
    isLevelVisible = false, // level is not displayed on the tasks screen
    onRepairClicked = {},
    onItemClicked = { onItemClicked(this) },
    onHelpIconClicked = {},
)