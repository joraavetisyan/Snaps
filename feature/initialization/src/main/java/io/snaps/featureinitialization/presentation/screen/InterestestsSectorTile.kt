package io.snaps.featureinitialization.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.unit.dp
import io.snaps.basesettings.data.model.InterestDto
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.model.Uuid
import io.snaps.coreuicompose.tools.TileState
import io.snaps.coreuicompose.uikit.button.SimpleChip
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.coreuicompose.uikit.other.ShimmerTile
import io.snaps.coreuitheme.compose.AppTheme

sealed class InterestsSectorTileState : TileState {

    data class Data(
        val interests: List<InterestDto>,
        val selectedIds: List<Uuid>,
        val onItemClick: (Uuid) -> Unit,
    ) : InterestsSectorTileState()

    object Shimmer : InterestsSectorTileState()

    data class Error(val onReloadClick: () -> Unit) : InterestsSectorTileState()

    @Composable
    override fun Content(modifier: Modifier) {
        InterestsSectorTile(modifier, this)
    }
}

@Composable
fun InterestsSectorTile(
    modifier: Modifier,
    data: InterestsSectorTileState,
) {
    when (data) {
        is InterestsSectorTileState.Shimmer -> Shimmer(modifier = modifier, data = data)
        is InterestsSectorTileState.Data -> Data(modifier = modifier, data = data)
        is InterestsSectorTileState.Error -> MessageBannerState.defaultState(onClick = data.onReloadClick)
    }
}

@Composable
private fun Shimmer(
    modifier: Modifier,
    data: InterestsSectorTileState.Shimmer,
) {
    Content(modifier = modifier) {
        repeat(12) {
            ShimmerTile(
                shape = AppTheme.shapes.medium,
                modifier = Modifier
                    .height(48.dp)
                    .width(120.dp)
                    .padding(end = 12.dp, bottom = 12.dp),
            )
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    FlowLayout(
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Data(
    modifier: Modifier,
    data: InterestsSectorTileState.Data,
) {
    Content(modifier = modifier) {
        data.interests.forEach { interest ->
            val selected = data.selectedIds.contains(interest.id)
            Box(
                modifier = Modifier.padding(end = 12.dp, bottom = 8.dp),
            ) {
                SimpleChip(
                    selected = selected,
                    onClick = { data.onItemClick(interest.id) },
                    label = interest.type.label,
                    textStyle = AppTheme.specificTypography.titleSmall,
                    leadingIcon = if (selected) {
                        AppTheme.specificIcons.checkBox.toImageValue()
                    } else interest.image.imageValue(),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = AppTheme.specificColorScheme.lightGrey,
                        labelColor = AppTheme.specificColorScheme.textPrimary,
                        selectedContainerColor = AppTheme.specificColorScheme.uiAccent,
                        selectedLabelColor = AppTheme.specificColorScheme.white,
                        selectedLeadingIconColor = AppTheme.specificColorScheme.white,
                        iconColor = Color.Unspecified,
                    ),
                    contentPadding = PaddingValues(8.dp),
                )
            }
        }
    }
}

private fun flowLayoutMeasurePolicy() = MeasurePolicy { measurables, constraints ->
    layout(constraints.maxWidth, constraints.maxHeight) {
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }
        var yPos = 0
        var xPos = 0
        var maxY = 0
        placeables.forEach { placeable ->
            if (xPos + placeable.width >
                constraints.maxWidth
            ) {
                xPos = 0
                yPos += maxY
                maxY = 0
            }
            placeable.placeRelative(
                x = xPos,
                y = yPos
            )
            xPos += placeable.width
            if (maxY < placeable.height) {
                maxY = placeable.height
            }
        }
    }
}

@Composable
private fun FlowLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val measurePolicy = flowLayoutMeasurePolicy()
    Layout(
        measurePolicy = measurePolicy,
        content = content,
        modifier = modifier,
    )
}