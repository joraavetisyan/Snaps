package io.snaps.featurecollection.presentation

import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.featurecollection.domain.NftItem
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
    price = price,
    image = image,
    dailyReward = dailyReward,
    dailyUnlock = dailyUnlock,
    dailyConsumption = dailyConsumption,
    isSelected = isSelected,
    clickListener = { onItemClicked(this) },
)

fun State<NftModel>.toNftCollectionItemState(
    onAddItemClicked: () -> Unit,
    onReloadClicked: () -> Unit,
) = when (this) {
    is Loading -> List(6) { CollectionItemState.Shimmer }
    is Effect -> when {
        isSuccess -> buildList<CollectionItemState> {
            requireData.items.forEach {
                add(it.toNftCollectionItemState())
            }
            if (requireData.maxCount > requireData.items.size) {
                add(CollectionItemState.AddItem(onAddItemClicked))
            }
        }
        else -> listOf(CollectionItemState.Error(onClick = onReloadClicked))
    }
}

fun State<NftModel>.toMysteryBoxCollectionItemState(
    onAddItemClicked: () -> Unit,
    onReloadClicked: () -> Unit,
) = when (this) {
    is Loading -> List(6) { CollectionItemState.Shimmer }
    is Effect -> when {
        isSuccess -> buildList<CollectionItemState> {
            requireData.items.forEach {
                add(it.toMysteryBoxCollectionItemState())
            }
            if (requireData.maxCount > requireData.items.size) {
               add(CollectionItemState.AddItem(onAddItemClicked))
            }
        }
        else -> listOf(CollectionItemState.Error(onClick = onReloadClicked))
    }
}

private fun NftItem.toNftCollectionItemState() = CollectionItemState.Nft(
    type = type,
    price = price,
    image = image,
    dailyReward = dailyReward,
    dailyUnlock = dailyUnlock,
    dailyConsumption = dailyConsumption,
)

private fun NftItem.toMysteryBoxCollectionItemState() = CollectionItemState.MysteryBox(
    image = image,
)