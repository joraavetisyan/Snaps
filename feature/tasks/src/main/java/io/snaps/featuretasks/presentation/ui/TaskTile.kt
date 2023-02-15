package io.snaps.featuretasks.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.snaps.baseprofile.ui.EnergyWidget
import io.snaps.corecommon.model.Uuid
import io.snaps.coreuicompose.tools.TileState
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
import io.snaps.coreuicompose.uikit.other.SimpleCard
import io.snaps.coreuitheme.compose.AppTheme

sealed class TaskTileState : TileState {

    data class Data(
        val id: Uuid,
        val title: String,
        val description: String,
        val energy: Int,
        val energyProgress: Int,
        val done: Boolean,
        val clickListener: () -> Unit,
    ) : TaskTileState()

    object Shimmer : TaskTileState()

    data class Error(val clickListener: () -> Unit) : TaskTileState()

    @Composable
    override fun Content(modifier: Modifier) {
        TaskTile(modifier, this)
    }
}

@Composable
fun TaskTile(
    modifier: Modifier = Modifier,
    data: TaskTileState,
) {
    when (data) {
        is TaskTileState.Data -> Data(modifier, data)
        TaskTileState.Shimmer -> Shimmer(modifier)
        is TaskTileState.Error -> MessageBannerState.defaultState(onClick = data.clickListener)
    }
}

@Composable
private fun Data(
    modifier: Modifier = Modifier,
    data: TaskTileState.Data,
) {
    Container(
        modifier = modifier.defaultTileRipple(onClick = data.clickListener),
    ) {
        Column(
            modifier = modifier
                .weight(1f)
                .padding(end = 8.dp),
        ) {
            Text(
                text = data.title,
                color = AppTheme.specificColorScheme.textPrimary,
                style = AppTheme.specificTypography.bodyMedium,
            )
            Text(
                text = data.description,
                color = AppTheme.specificColorScheme.textSecondary,
                style = AppTheme.specificTypography.bodySmall,
            )
        }
        EnergyWidget(
            value = "${data.energyProgress}/${data.energy}",
            isFull = data.done,
        )
    }
}

@Composable
private fun Shimmer(
    modifier: Modifier = Modifier,
) {
    Container(modifier = modifier) {
        MiddlePart.Shimmer(
            needValueLine = true,
            needDescriptionLine = true,
        ).Content(modifier = Modifier)
        RightPart.Shimmer(
            needRightLine = true,
        ).Content(modifier = Modifier)
    }
}

@Composable
private fun Container(
    modifier: Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    SimpleCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            content = content,
        )
    }
}