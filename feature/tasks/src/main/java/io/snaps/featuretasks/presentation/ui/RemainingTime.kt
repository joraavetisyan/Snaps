package io.snaps.featuretasks.presentation.ui

import android.os.CountDownTimer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.date.toTimeFormat
import io.snaps.coreuicompose.tools.TileState
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.other.ShimmerTile
import io.snaps.coreuicompose.uikit.other.ShimmerTileConfig
import io.snaps.coreuicompose.uikit.other.ShimmerTileLine
import io.snaps.coreuitheme.compose.AppTheme

sealed class RemainingTimeTileState : TileState {

    data class Data(
        val time: Long,
        val energy: Int,
        val energyProgress: Int,
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
    val milliseconds = data.time - System.currentTimeMillis()
    val time = remember { mutableStateOf(milliseconds.toTimeFormat()) }
    val countDownTimer = object : CountDownTimer(milliseconds, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            time.value = millisUntilFinished.toTimeFormat()
        }
        override fun onFinish() = Unit
    }

    DisposableEffect(key1 = Unit) {
        countDownTimer.start()
        onDispose {
            countDownTimer.cancel()
        }
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = io.snaps.corecommon.strings.StringKey.TasksTitleRemainingTime.textValue().get(),
            color = AppTheme.specificColorScheme.textSecondary,
            style = AppTheme.specificTypography.labelMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            text = time.value,
            style = AppTheme.specificTypography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
        TaskProgress(
            progress = data.energyProgress,
            maxValue = data.energy,
        )
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