package io.snaps.basenft.ui

import io.snaps.corecommon.R
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.model.FiatValue
import io.snaps.corecommon.model.NftType

fun FiatValue?.rankCostToString() = when {
    this == null -> ""
    this.value == 0.0 -> "Free" // todo localize?
    else -> getFormatted()
}

fun NftType.getSunglassesImage() = when (this) {
    NftType.Free -> R.drawable.img_sunglasses0
    NftType.Newbie -> R.drawable.img_sunglasses1
    NftType.Viewer -> R.drawable.img_sunglasses2
    NftType.Follower -> R.drawable.img_sunglasses3
    NftType.Sub -> R.drawable.img_sunglasses4
    NftType.Sponsor -> R.drawable.img_sunglasses5
    NftType.Influencer -> R.drawable.img_sunglasses6
    NftType.FamousGuy -> R.drawable.img_sunglasses7
    NftType.Star -> R.drawable.img_sunglasses8
    NftType.Rockstar -> R.drawable.img_sunglasses9
    NftType.SuperStar -> R.drawable.img_sunglasses10
    NftType.Legend -> R.drawable.img_sunglasses10
}.imageValue()