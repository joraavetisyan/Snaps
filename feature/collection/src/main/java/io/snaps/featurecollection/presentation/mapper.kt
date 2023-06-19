package io.snaps.featurecollection.presentation

import io.snaps.basenft.domain.MysteryBoxModel
import io.snaps.basenft.domain.NftModel
import io.snaps.basenft.domain.RankModel
import io.snaps.basenft.ui.CollectionItemState
import io.snaps.corecommon.ext.toPercentageFormat
import io.snaps.corecommon.model.CoinSNPS
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.featurecollection.presentation.screen.MysteryBoxTileState
import io.snaps.featurecollection.presentation.screen.RankTileState

val likeValue get() = CoinSNPS(0.5)

fun State<List<RankModel>>.toRankTileState(
    snpsUsdExchangeRate: Double,
    onItemClicked: (RankModel) -> Unit,
    onReloadClicked: () -> Unit,
) = when (this) {
    is Loading -> List(6) { RankTileState.Shimmer }
    is Effect -> when {
        isSuccess -> {
            requireData.map {
                it.toRankTileState(
                    snpsUsdExchangeRate = snpsUsdExchangeRate,
                    onItemClicked = onItemClicked,
                )
            }
        }
        else -> listOf(RankTileState.Error(onClick = onReloadClicked))
    }
}

private fun RankModel.toRankTileState(
    snpsUsdExchangeRate: Double,
    onItemClicked: (RankModel) -> Unit,
) = RankTileState.Data(
    type = type,
    cost = cost,
    image = image,
    additionalData = additionalData,
    dailyReward = dailyReward.toFiat(rate = snpsUsdExchangeRate),
    dailyUnlock = dailyUnlock.toPercentageFormat(),
    dailyConsumption = dailyConsumption.toPercentageFormat(),
    isPurchasable = isPurchasable,
    clickListener = { onItemClicked(this) },
)

fun State<List<NftModel>>.toNftCollectionItemState(
    snpsUsdExchangeRate: Double,
    onItemClicked: (NftModel) -> Unit,
    onAddItemClicked: () -> Unit,
    onReloadClicked: () -> Unit,
    onRepairClicked: (NftModel) -> Unit,
    onHelpIconClicked: () -> Unit,
) = when (this) {
    is Loading -> List(6) { CollectionItemState.Shimmer }
    is Effect -> when {
        isSuccess -> buildList<CollectionItemState> {
            this += requireData.map {
                it.toNftCollectionItemState(
                    snpsUsdExchangeRate = snpsUsdExchangeRate,
                    onRepairClicked = onRepairClicked,
                    onItemClicked = onItemClicked,
                    onHelpIconClicked = onHelpIconClicked,
                )
            }
            this += CollectionItemState.AddItem(onAddItemClicked)
        }
        else -> listOf(CollectionItemState.Error(onClick = onReloadClicked))
    }
}

private fun NftModel.toNftCollectionItemState(
    snpsUsdExchangeRate: Double,
    onItemClicked: (NftModel) -> Unit,
    onRepairClicked: (NftModel) -> Unit,
    onHelpIconClicked: () -> Unit,
) = CollectionItemState.Nft(
    type = type,
    image = image,
    dailyReward = dailyReward.toFiat(rate = snpsUsdExchangeRate),
    dailyUnlock = dailyUnlock.toPercentageFormat(),
    dailyConsumption = dailyConsumption.toPercentageFormat(),
    isHealthBadgeVisible = !isHealthy,
    isRepairable = !isHealthy,
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

fun State<List<MysteryBoxModel>>.toMysteryBoxTileState(
    onItemClicked: (MysteryBoxModel) -> Unit,
    onReloadClicked: () -> Unit,
) = when (this) {
    is Loading -> List(2) { MysteryBoxTileState.Shimmer }
    is Effect -> when {
        isSuccess -> {
            requireData.map {
                it.toMysteryBoxTileState(
                    onItemClicked = onItemClicked,
                )
            }
        }
        else -> listOf(MysteryBoxTileState.Error(onClick = onReloadClicked))
    }
}

private fun MysteryBoxModel.toMysteryBoxTileState(
    onItemClicked: (MysteryBoxModel) -> Unit,
) = MysteryBoxTileState.Data(
    type = type,
    cost = fiatCost,
    probabilities = marketingProbabilities,
    clickListener = { onItemClicked(this) },
)