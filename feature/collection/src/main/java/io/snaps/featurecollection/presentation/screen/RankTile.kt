package io.snaps.featurecollection.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.snaps.baseprofile.ui.ValueWidget
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.TileState
import io.snaps.coreuicompose.tools.addIf
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.other.ShimmerTile
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder

sealed class RankTileState : TileState {

    data class Data(
        val type: NftType,
        val price: String,
        val image: ImageValue,
        val dailyReward: String,
        val dailyUnlock: String,
        val dailyConsumption: String,
        val isAvailableToPurchase: Boolean,
        val clickListener: () -> Unit,
    ) : RankTileState()

    object Shimmer : RankTileState()

    data class Error(val onClick: () -> Unit) : RankTileState()

    @Composable
    override fun Content(modifier: Modifier) {
       RankTile(modifier = modifier, data = this)
    }
}

@Composable
private fun RankTile(
    modifier: Modifier,
    data: RankTileState,
) {
    when (data) {
        is RankTileState.Shimmer -> Shimmer(modifier = modifier)
        is RankTileState.Error -> MessageBannerState.defaultState(onClick = data.onClick)
        is RankTileState.Data -> Data(modifier, data)
    }
}

@Composable
private fun Data(
    modifier: Modifier,
    data: RankTileState.Data,
) {
    Container(
        modifier = modifier
            .addIf(!data.isAvailableToPurchase) {
                defaultTileRipple(onClick = data.clickListener, padding = 0.dp)
            }.addIf(data.isAvailableToPurchase) {
                drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            color = Color.White.copy(alpha = 0.5f),
                        )
                    }
                }
            },
    ) {
        Image(
            data.image.get(),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Spacer(Modifier.width(8.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = data.type.name,
                    style = AppTheme.specificTypography.labelMedium,
                )
                ValueWidget(ImageValue.ResImage(R.drawable.img_coin_silver) to data.price)
            }
            Spacer(Modifier.height(4.dp))
            Line(
                name = LocalStringHolder.current(StringKey.RankSelectionTitleDailyReward),
                value = data.dailyReward,
            )
            Line(
                name = LocalStringHolder.current(StringKey.RankSelectionTitleDailyUnlock),
                value = data.dailyUnlock,
            )
            Line(
                name = LocalStringHolder.current(StringKey.RankSelectionTitleDailyConsumption),
                value = data.dailyConsumption,
            )
        }
    }
}

@Composable
private fun Line(name: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = name,
            style = AppTheme.specificTypography.bodySmall,
            color = AppTheme.specificColorScheme.textSecondary,
        )
        Text(text = value, style = AppTheme.specificTypography.bodySmall)
    }
}

@Composable
private fun Shimmer(
    modifier: Modifier,
) {
    Container(modifier.fillMaxWidth()) {
        ShimmerTile(
            modifier = Modifier
                .size(100.dp)
                .padding(end = 8.dp),
            shape = AppTheme.shapes.small,
        )
        Spacer(modifier = Modifier.weight(1f))
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            repeat(4) {
                MiddlePart.Shimmer(needValueLine = true).Content(modifier = Modifier)
            }
        }
    }
}

@Composable
private fun Container(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.specificColorScheme.white,
        ),
        border = BorderStroke(width = 1.dp, color = AppTheme.specificColorScheme.darkGrey.copy(0.5f)),
        shape = AppTheme.shapes.medium,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}