package io.snaps.featurecollection.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.FiatValue
import io.snaps.corecommon.model.BundleType
import io.snaps.corecommon.model.FiatUSD
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.TileState
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.coreuicompose.uikit.other.ShimmerTile
import io.snaps.coreuicompose.uikit.other.ShimmerTileConfig
import io.snaps.coreuicompose.uikit.other.ShimmerTileLine
import io.snaps.coreuitheme.compose.AppTheme

sealed class BundleTileState : TileState {

    data class Data(
        val type: BundleType,
        val cost: FiatValue,
        val discount: FiatValue,
        val clickListener: () -> Unit,
    ) : BundleTileState()

    object Shimmer : BundleTileState()

    data class Error(val onClick: () -> Unit) : BundleTileState()

    @Composable
    override fun Content(modifier: Modifier) {
       BundleTile(modifier = modifier, data = this)
    }
}

@Composable
private fun BundleTile(
    modifier: Modifier,
    data: BundleTileState,
) {
    when (data) {
        is BundleTileState.Shimmer -> Shimmer(modifier = modifier)
        is BundleTileState.Error -> MessageBannerState.defaultState(onClick = data.onClick)
        is BundleTileState.Data -> Data(modifier, data)
    }
}

@Composable
private fun Data(
    modifier: Modifier,
    data: BundleTileState.Data,
) {
    val totalCostText = buildAnnotatedString {
        val totalCost = data.cost.getFormatted()
        val cost = FiatUSD(data.cost.value + data.discount.value).getFormatted()
        val textFormatted = "$cost $totalCost"
        append(textFormatted)
        val startIndex = textFormatted.indexOf(cost)
        val endIndex = startIndex + cost.length
        addStyle(
            style = SpanStyle(
                color = AppTheme.specificColorScheme.textSecondary,
                textDecoration = TextDecoration.LineThrough,
            ),
            start = startIndex,
            end = endIndex,
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultTileRipple(onClick = data.clickListener, padding = 0.dp),
    ) {
        Image(
            painter = data.type.getBundleImage().get(),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .matchParentSize()
                .clip(AppTheme.shapes.medium),
        )
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = data.type.value,
                style = AppTheme.specificTypography.headlineMedium,
                color = AppTheme.specificColorScheme.white,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = StringKey.RankSelectionMessageBundle.textValue().get(),
                style = AppTheme.specificTypography.titleSmall,
                color = AppTheme.specificColorScheme.white,
            )
            Text(
                text = StringKey.RankSelectionFieldDiscount.textValue(data.discount.getFormatted()).get(),
                color = AppTheme.specificColorScheme.white,
                style = AppTheme.specificTypography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(
                        color = AppTheme.specificColorScheme.white_10,
                        shape = AppTheme.shapes.medium
                    )
                    .border(
                        width = 1.dp,
                        color = AppTheme.specificColorScheme.white_30,
                        shape = AppTheme.shapes.medium
                    )
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            )
            Text(
                text = totalCostText,
                color = AppTheme.specificColorScheme.uiAccent,
                style = AppTheme.specificTypography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(160.dp)
                    .background(
                        color = AppTheme.specificColorScheme.white,
                        shape = AppTheme.shapes.medium
                    )
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun Shimmer(
    modifier: Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = AppTheme.specificColorScheme.white, shape = AppTheme.shapes.medium)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ShimmerTileLine(
            width = ShimmerTileConfig.WidthExtraLarge,
            height = AppTheme.specificTypography.headlineMedium.lineHeight.value.dp,
        )
        ShimmerTileLine(
            width = ShimmerTileConfig.WidthMedium,
            height = AppTheme.specificTypography.titleSmall.lineHeight.value.dp,
        )
        repeat(2) {
            ShimmerTile(
                modifier = Modifier
                    .width(160.dp)
                    .height(48.dp),
                shape = AppTheme.shapes.small,
            )
        }
    }
}