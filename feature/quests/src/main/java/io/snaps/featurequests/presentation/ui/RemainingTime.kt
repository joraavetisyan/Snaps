package io.snaps.featurequests.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.date.toTimeFormat
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.TileState
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.other.ShimmerTile
import io.snaps.coreuicompose.uikit.other.ShimmerTileConfig
import io.snaps.coreuicompose.uikit.other.ShimmerTileLine
import io.snaps.coreuitheme.compose.AppTheme
import kotlin.time.Duration

sealed class RemainingTimeTileState : TileState {

    data class Data(
        val time: Duration,
    ) : RemainingTimeTileState()

    object Shimmer : RemainingTimeTileState()

    @Composable
    override fun Content(modifier: Modifier) {
        RemainingTimeTile(modifier, this)
    }
}

@Composable
fun RemainingTimeTile(
    modifier: Modifier = Modifier,
    data: RemainingTimeTileState,
) {
    when (data) {
        is RemainingTimeTileState.Data -> Data(modifier, data)
        RemainingTimeTileState.Shimmer -> Shimmer(modifier)
    }
}

@Composable
private fun Data(
    modifier: Modifier = Modifier,
    data: RemainingTimeTileState.Data,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = StringKey.TasksTitleRemainingTime.textValue().get(),
            color = AppTheme.specificColorScheme.textSecondary,
            style = AppTheme.specificTypography.labelMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            text = data.time.toTimeFormat(),
            style = AppTheme.specificTypography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun Shimmer(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ShimmerTileLine(
            width = ShimmerTileConfig.WidthMedium,
            height = AppTheme.specificTypography.labelMedium.lineHeight.value.dp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        ShimmerTileLine(
            width = ShimmerTileConfig.WidthSmall,
            height = AppTheme.specificTypography.titleLarge.lineHeight.value.dp,
        )
        Spacer(modifier = Modifier.height(12.dp))
        ShimmerTile(
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
        )
    }
}