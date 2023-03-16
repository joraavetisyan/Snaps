package io.snaps.featurecollection.presentation

import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.FiatCurrency
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.featurecollection.domain.NftModel
import io.snaps.featurecollection.domain.RankModel
import io.snaps.featurecollection.presentation.screen.CollectionItemState
import io.snaps.featurecollection.presentation.screen.RankTileState

fun Effect<List<RankModel>>.toRankTileState(
    onItemClicked: (RankModel) -> Unit,
    onReloadClicked: () -> Unit,
) = when {
    isSuccess -> {
        requireData.map {
            it.toRankTileState(onItemClicked = onItemClicked)
        }
    }
    else -> listOf(RankTileState.Error(onClick = onReloadClicked))
}

private fun RankModel.toRankTileState(
    onItemClicked: (RankModel) -> Unit,
) = RankTileState.Data(
    type = type,
    price = if (costInUsd == 0) "Free" else "$costInUsd${FiatCurrency.USD.symbol}",
    image = image,
    dailyReward = "${dailyReward * 100}${FiatCurrency.USD.symbol}",
    dailyUnlock = "${dailyUnlock * 100}%",
    dailyConsumption = "${dailyConsumption * 100}%",
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
    price = if (costInUsd == 0) "Free" else "$costInUsd${FiatCurrency.USD.symbol}",
    image = image,
    dailyReward = "${dailyReward * 100}${FiatCurrency.USD.symbol}",
    dailyUnlock = "${dailyUnlock * 100}%",
    dailyConsumption = "${dailyConsumption * 100}%",
)

private fun NftModel.toMysteryBoxCollectionItemState() = CollectionItemState.MysteryBox(
    image = image,
)