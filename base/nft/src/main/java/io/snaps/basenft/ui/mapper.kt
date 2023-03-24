package io.snaps.basenft.ui

import io.snaps.basenft.domain.NftModel
import io.snaps.corecommon.ext.toPercentageFormat
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.FiatCurrency
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State

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

private fun NftModel.toNftCollectionItemState() = CollectionItemState.Nft(
    type = type,
    price = costInUsd?.costToString() ?: "",
    image = image,
    dailyReward = "$dailyReward${FiatCurrency.USD.symbol}",
    dailyUnlock = dailyUnlock.toPercentageFormat(),
    dailyConsumption = dailyUnlock.toPercentageFormat(),
)

fun Int.costToString() = if (this == 0) "Free" else "$this${FiatCurrency.USD.symbol}"