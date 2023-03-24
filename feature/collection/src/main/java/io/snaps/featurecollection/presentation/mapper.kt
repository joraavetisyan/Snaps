package io.snaps.featurecollection.presentation

import io.snaps.corecommon.ext.toPercentageFormat
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.FiatCurrency
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.featurecollection.domain.NftModel
import io.snaps.featurecollection.domain.RankModel
import io.snaps.featurecollection.presentation.screen.CollectionItemState
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
) = when (this) {
    is Loading -> List(6) { CollectionItemState.Shimmer }
    is Effect -> when {
        isSuccess -> buildList<CollectionItemState> {
            requireData.forEach {
                add(it.toNftCollectionItemState())
            }
            if (maxCount > requireData.size) {
                add(CollectionItemState.AddItem(onAddItemClicked))
            }
        }
        else -> listOf(CollectionItemState.Error(onClick = onReloadClicked))
    }
}

fun State<List<NftModel>>.toMysteryBoxCollectionItemState(
    maxCount: Int,
    onAddItemClicked: () -> Unit,
    onReloadClicked: () -> Unit,
) = when (this) {
    is Loading -> List(6) { CollectionItemState.Shimmer }
    is Effect -> when {
        isSuccess -> buildList<CollectionItemState> {
            requireData.forEach {
                add(it.toMysteryBoxCollectionItemState())
            }
            if (maxCount > requireData.size) {
                add(CollectionItemState.AddItem(onAddItemClicked))
            }
        }
        else -> listOf(CollectionItemState.Error(onClick = onReloadClicked))
    }
}

private fun NftModel.toNftCollectionItemState() = CollectionItemState.Nft(
    type = type,
    price = costInUsd?.costToString() ?: "",
    image = image,
    dailyReward = "$dailyReward${FiatCurrency.USD.symbol}",
    dailyUnlock = dailyUnlock.toPercentageFormat(),
    dailyConsumption = dailyUnlock.toPercentageFormat(),
)

private fun NftModel.toMysteryBoxCollectionItemState() = CollectionItemState.MysteryBox(
    image = image,
)

private fun Int.costToString() = if (this == 0) "Free" else "$this${FiatCurrency.USD.symbol}"