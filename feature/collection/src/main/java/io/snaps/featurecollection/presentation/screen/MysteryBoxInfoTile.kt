package io.snaps.featurecollection.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.snaps.baseprofile.ui.ValueWidget
import io.snaps.corecommon.R
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.FiatValue
import io.snaps.corecommon.model.MysteryBoxType
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.TileState
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.coreuicompose.uikit.other.ShimmerTile
import io.snaps.coreuicompose.uikit.other.ShimmerTileConfig
import io.snaps.coreuicompose.uikit.other.ShimmerTileLine
import io.snaps.coreuitheme.compose.AppTheme

sealed class MysteryBoxInfoTileState : TileState {

    data class Data(
        val type: MysteryBoxType,
        val cost: FiatValue,
    ) : MysteryBoxInfoTileState()

    object Shimmer : MysteryBoxInfoTileState()

    data class Error(val onClick: () -> Unit) : MysteryBoxInfoTileState()

    @Composable
    override fun Content(modifier: Modifier) {
        MysteryBoxInfoTile(modifier = modifier, data = this)
    }
}

@Composable
private fun MysteryBoxInfoTile(
    modifier: Modifier,
    data: MysteryBoxInfoTileState,
) {
    when (data) {
        is MysteryBoxInfoTileState.Shimmer -> Shimmer(modifier = modifier)
        is MysteryBoxInfoTileState.Error -> MessageBannerState.defaultState(onClick = data.onClick)
        is MysteryBoxInfoTileState.Data -> Data(modifier, data)
    }
}

@Composable
private fun Data(
    modifier: Modifier,
    data: MysteryBoxInfoTileState.Data,
) {
    Container(modifier = modifier) {
        Image(
            painter = when (data.type) {
                MysteryBoxType.FirstTier -> R.drawable.img_mystery_box
                MysteryBoxType.SecondTier -> R.drawable.img_prime_box
            }.imageValue().get(),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .clip(AppTheme.shapes.medium),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = when (data.type) {
                MysteryBoxType.FirstTier -> StringKey.MysteryBoxTitleGuaranteed.textValue(NftType.Follower.displayName)
                MysteryBoxType.SecondTier -> StringKey.MysteryBoxTitleGuaranteed.textValue(NftType.Sponsor.displayName)
            }.get(),
            style = AppTheme.specificTypography.bodyMedium,
            color = AppTheme.specificColorScheme.textSecondary,
        )
        Text(
            text = when (data.type) {
                MysteryBoxType.FirstTier -> StringKey.MysteryBoxFieldDropChance.textValue(NftType.Sponsor.displayName)
                MysteryBoxType.SecondTier -> StringKey.MysteryBoxFieldDropChance.textValue(NftType.Legend.displayName)
            }.get(),
            style = AppTheme.specificTypography.bodyMedium,
            color = AppTheme.specificColorScheme.textSecondary,
        )
        Spacer(modifier = Modifier.height(12.dp))
        ValueWidget(R.drawable.img_coin_bronze.imageValue() to data.cost.getFormatted().textValue())
    }
}

@Composable
private fun Shimmer(
    modifier: Modifier,
) {
    Container(modifier.fillMaxWidth()) {
        ShimmerTile(
            modifier = Modifier.size(200.dp),
            shape = AppTheme.shapes.medium,
        )
        Spacer(modifier = Modifier.height(12.dp))
        ShimmerTileLine(
            width = ShimmerTileConfig.WidthMedium,
            height = AppTheme.specificTypography.bodyMedium.lineHeight.value.dp,
        )
        ShimmerTileLine(
            width = ShimmerTileConfig.WidthMedium,
            height = AppTheme.specificTypography.bodyMedium.lineHeight.value.dp,
        )
        Spacer(modifier = Modifier.height(12.dp))
        ShimmerTileLine(
            width = ShimmerTileConfig.WidthExtraSmall,
            height = 32.dp,
        )
    }
}

@Composable
private fun Container(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppTheme.specificColorScheme.white),
        shape = AppTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(),
        modifier = modifier.shadow(elevation = 16.dp, shape = AppTheme.shapes.medium),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content,
        )
    }
}