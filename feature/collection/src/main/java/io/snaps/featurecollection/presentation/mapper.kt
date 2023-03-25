package io.snaps.featurecollection.presentation

import io.snaps.basenft.domain.NftModel
import io.snaps.basenft.domain.RankModel
import io.snaps.basenft.ui.CollectionItemState
import io.snaps.basenft.ui.costToString
import io.snaps.corecommon.ext.toPercentageFormat
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.FiatCurrency
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.corecommon.model.Uuid
import io.snaps.featurecollection.presentation.screen.RankTileState

fun State<List<RankModel>>.toRankTileState(
    purchasedRanks: List<NftModel>,
    onItemClicked: (RankModel) -> Unit,
    onReloadClicked: () -> Unit,
) = when (this) {
    is Loading -> List(6) { RankTileState.Shimmer }
    is Effect -> when {
        isSuccess -> {
            requireData.map { rank ->
                rank.copy(
                    isAvailableToPurchase = rank.isAvailableToPurchase && purchasedRanks.none {
                        it.type == rank.type
                    }
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
    price = costInUsd?.costToString() ?: "",
    image = image,
    dailyReward = "$dailyReward${FiatCurrency.USD.symbol}",
    dailyUnlock = dailyUnlock.toPercentageFormat(),
    dailyConsumption = dailyUnlock.toPercentageFormat(),
    isAvailableToPurchase = isAvailableToPurchase,
    clickListener = { onItemClicked(this) },
)

fun State<List<NftModel>>.toNftCollectionItemState(
    maxCount: Int,
    onAddItemClicked: () -> Unit,
    onReloadClicked: () -> Unit,
    onRepairClicked: (Uuid) -> Unit,
) = when (this) {
    is Loading -> List(6) { CollectionItemState.Shimmer }
    is Effect -> when {
        isSuccess -> buildList<CollectionItemState> {
            requireData.forEach {
                add(it.toNftCollectionItemState(onRepairClicked))
            }
            if (maxCount > requireData.size) {
                add(CollectionItemState.AddItem(onAddItemClicked))
            }
        }
        else -> listOf(CollectionItemState.Error(onClick = onReloadClicked))
    }
}

private fun NftModel.toNftCollectionItemState(
    onRepairClicked: (Uuid) -> Unit,
) = CollectionItemState.Nft(
    type = type,
    price = costInUsd?.costToString() ?: "",
    image = image,
    dailyReward = "$dailyReward${FiatCurrency.USD.symbol}",
    dailyUnlock = dailyUnlock.toPercentageFormat(),
    dailyConsumption = dailyUnlock.toPercentageFormat(),
    isHealthy = isHealthy,
    onRepairClicked = { onRepairClicked(id) },
)