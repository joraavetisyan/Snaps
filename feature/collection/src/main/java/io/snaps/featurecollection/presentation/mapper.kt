package io.snaps.featurecollection.presentation

import io.snaps.basenft.domain.NftModel
import io.snaps.basenft.domain.RankModel
import io.snaps.basenft.ui.CollectionItemState
import io.snaps.corecommon.ext.toPercentageFormat
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.featurecollection.presentation.screen.RankTileState

fun State<List<RankModel>>.toRankTileState(
    onItemClicked: (RankModel) -> Unit,
    onReloadClicked: () -> Unit,
) = when (this) {
    is Loading -> List(6) { RankTileState.Shimmer }
    is Effect -> when {
        isSuccess -> {
            requireData.map { rank ->
                rank.copy(
                    isPurchasable = rank.isPurchasable
                ).toRankTileState(onItemClicked = onItemClicked)
            }
        }
        else -> listOf(RankTileState.Error(onClick = onReloadClicked))
    }
}

private fun RankModel.toRankTileState(
    onItemClicked: (RankModel) -> Unit,
) = RankTileState.Data(
    type = type,
    cost = cost,
    image = image,
    dailyReward = dailyReward.toFiat(rate = 0.001),
    dailyUnlock = dailyUnlock.toPercentageFormat(),
    dailyConsumption = dailyConsumption.toPercentageFormat(),
    isPurchasable = isPurchasable,
    clickListener = { onItemClicked(this) },
)

fun State<List<NftModel>>.toNftCollectionItemState(
    onItemClicked: (NftModel) -> Unit,
    onAddItemClicked: () -> Unit,
    onReloadClicked: () -> Unit,
    onRepairClicked: (NftModel) -> Unit,
    onHelpIconClicked: () -> Unit,
) = when (this) {
    is Loading -> List(6) { CollectionItemState.Shimmer }
    is Effect -> when {
        isSuccess -> buildList<CollectionItemState> {
            requireData.forEach {
                add(
                    it.toNftCollectionItemState(
                        onRepairClicked = onRepairClicked,
                        onItemClicked = onItemClicked,
                        onHelpIconClicked = onHelpIconClicked,
                    )
                )
            }
            add(CollectionItemState.AddItem(onAddItemClicked))
        }
        else -> listOf(CollectionItemState.Error(onClick = onReloadClicked))
    }
}

private fun NftModel.toNftCollectionItemState(
    onItemClicked: (NftModel) -> Unit,
    onRepairClicked: (NftModel) -> Unit,
    onHelpIconClicked: () -> Unit,
) = CollectionItemState.Nft(
    type = type,
    image = image,
    dailyReward = dailyReward.toFiat(rate = 0.001),
    dailyUnlock = dailyUnlock.toPercentageFormat(),
    dailyConsumption = dailyConsumption.toPercentageFormat(),
    isHealthBadgeVisible = !isHealthy,
    isRepairVisible = !isHealthy,
    level = level,
    upperThreshold = upperThreshold,
    lowerThreshold = lowerThreshold,
    experience = experience,
    bonus = bonus,
    isLevelVisible = true,
    onRepairClicked = { onRepairClicked(this) },
    onItemClicked = { onItemClicked(this) },
    onHelpIconClicked = onHelpIconClicked,
)