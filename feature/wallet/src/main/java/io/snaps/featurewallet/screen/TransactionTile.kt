package io.snaps.featurewallet.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.snaps.baseprofile.data.model.TransactionType
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.model.Uuid
import io.snaps.coreuicompose.tools.TileState
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.listtile.CellTileContainer
import io.snaps.coreuicompose.uikit.other.ShimmerTileCircle
import io.snaps.coreuicompose.uikit.other.ShimmerTileConfig
import io.snaps.coreuicompose.uikit.other.ShimmerTileLine
import io.snaps.coreuitheme.compose.AppTheme

sealed class TransactionTileState(val key: Any) : TileState {

    data class Data(
        val id: Uuid,
        val type: TransactionType,
        val coins: TextValue,
        val icon: ImageValue,
        val dateTime: TextValue,
        val clickListener: () -> Unit,
    ) : TransactionTileState(id)

    data class Shimmer(
        private val index: Int,
    ) : TransactionTileState(-index)

    object Progress : TransactionTileState(-1000)

    @Composable
    override fun Content(modifier: Modifier) {
        TransactionTile(modifier, this)
    }
}

@Composable
fun TransactionTile(
    modifier: Modifier = Modifier,
    data: TransactionTileState,
) {
    when (data) {
        is TransactionTileState.Data -> Data(data = data, modifier = modifier)
        is TransactionTileState.Shimmer -> Shimmer(modifier = modifier)
        is TransactionTileState.Progress -> CellTileContainer(modifier = modifier) {
            Box(modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center),
                )
            }
        }
    }
}

@Composable
private fun Shimmer(
    modifier: Modifier = Modifier,
) {
    Container(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ShimmerTileCircle(
                size = 32.dp,
                modifier = Modifier.padding(end = 8.dp),
            )
            ShimmerTileLine(
                width = 32.dp,
                height = AppTheme.specificTypography.labelMedium.lineHeight.value.dp,
            )
        }
        ShimmerTileLine(
            width = 32.dp,
            height = AppTheme.specificTypography.bodyMedium.lineHeight.value.dp,
        )
        ShimmerTileLine(
            width = ShimmerTileConfig.WidthExtraSmall,
            height = AppTheme.specificTypography.bodyMedium.lineHeight.value.dp,
        )
    }
}

@Composable
private fun Data(
    data: TransactionTileState.Data,
    modifier: Modifier,
) {
    Container(modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = data.icon.get(),
                tint = Color.Unspecified,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
            )
            Text(
                text = data.type.title,
                style = AppTheme.specificTypography.labelMedium,
            )
        }
        Text(
            text = data.coins.get(),
            style = AppTheme.specificTypography.bodySmall,
        )
        Text(
            text = data.dateTime.get(),
            style = AppTheme.specificTypography.bodySmall,
        )
    }
}

@Composable
private fun Container(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = AppTheme.specificColorScheme.darkGrey.copy(alpha = 0.5f),
                shape = AppTheme.shapes.medium,
            )
            .background(
                color = AppTheme.specificColorScheme.white,
                shape = AppTheme.shapes.medium,
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        content = content,
    )
}