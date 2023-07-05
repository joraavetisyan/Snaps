package io.snaps.featurequests.presentation

import io.snaps.basenft.domain.NftModel
import io.snaps.basenft.ui.CollectionItemState
import io.snaps.basequests.data.model.SocialPostStatus
import io.snaps.basequests.domain.QuestInfoModel
import io.snaps.basequests.domain.QuestModel
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.toPercentageFormat
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.QuestType
import io.snaps.corecommon.model.State
import io.snaps.corecommon.strings.StringKey
import io.snaps.featurequests.presentation.ui.QuestStatus
import io.snaps.featurequests.presentation.ui.QuestTileState
import io.snaps.featurequests.presentation.ui.RemainingTimeTileState
import kotlin.time.Duration

fun State<QuestModel>.toTaskTileState(
    onItemClicked: (QuestInfoModel) -> Unit,
    onReloadClicked: () -> Unit,
) = when (this) {
    is Loading -> List(6) { QuestTileState.Shimmer }
    is Effect -> when {
        isSuccess -> requireData.quests.map {
            it.toTaskTileState(onItemClicked = onItemClicked)
        }
        else -> listOf(QuestTileState.Error(clickListener = onReloadClicked))
    }
}

private fun QuestInfoModel.toTaskTileState(
    onItemClicked: (QuestInfoModel) -> Unit,
) = QuestTileState.Data(
    energy = energy,
    energyProgress = this.energyProgress(),
    title = type.toQuestTitle(count),
    description = type.toQuestDescription(count),
    status = status(),
    clickListener = { onItemClicked(this) },
)

fun taskDefaultCount(type: QuestType) = when (type) {
    QuestType.Like -> 10
    QuestType.PublishVideo -> -1
    QuestType.Subscribe -> 5
    QuestType.Watch -> 20
    QuestType.SocialShare -> -1
    QuestType.SocialPost -> -1
}

fun State<QuestModel>.toRemainingTimeTileState(
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
fun QuestInfoModel.energyProgress(): Int {
    return if (madeCount != null && count != null) {
        val madeByOne = madeCount!!.toDouble() / count!!.toDouble() * energy
        madeByOne.toInt()
    } else {
        if (completed) {
            energy
        } else 0
    }
}

fun QuestType.toQuestTitle(count: Int?) = when (this) {
    QuestType.Like -> StringKey.TasksTitleLike.textValue()
    QuestType.PublishVideo -> StringKey.TasksTitlePublishVideo.textValue()
    QuestType.Subscribe -> StringKey.TasksTitleSubscribe.textValue()
    QuestType.Watch -> StringKey.TasksTitleWatchVideo.textValue((count ?: taskDefaultCount(this)).toString())
    else -> StringKey.TasksTitleSocialPost.textValue()
}

fun QuestType.toQuestDescription(count: Int?) = when (this) {
    QuestType.Like -> StringKey.TasksMessageLike.textValue()
    QuestType.PublishVideo -> StringKey.TasksMessagePublishVideo.textValue()
    QuestType.Subscribe -> StringKey.TasksMessageSubscribe.textValue()
    QuestType.Watch -> StringKey.TasksMessageWatchVideo.textValue((count ?: taskDefaultCount(this)).toString())
    else -> StringKey.TasksMessageSocialPost.textValue()
}

fun QuestInfoModel.status(): QuestStatus? = when (status) {
    SocialPostStatus.Success -> QuestStatus.Credited
    SocialPostStatus.NotPosted -> QuestStatus.NotPosted
    SocialPostStatus.WaitForVerification -> QuestStatus.WaitForVerification
    SocialPostStatus.Rejected -> QuestStatus.Rejected
    null -> if (energyProgress() == energy) {
        QuestStatus.Credited
    } else if (energyProgress() > 0) {
        QuestStatus.InProgress
    } else null
    SocialPostStatus.NotSendToVerify -> QuestStatus.NotSendToVerify
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