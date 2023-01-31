package com.defince.coreuicompose.uikit.listtile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.defince.coreuicompose.tools.TileState
import com.defince.coreuicompose.tools.addIf
import com.defince.coreuicompose.tools.doOnClick
import com.defince.coreuicompose.uikit.other.Badge
import com.defince.coreuitheme.compose.AppTheme
import com.defince.coreuitheme.compose.SpecificValues

data class CellTileState(
    val hasBadge: Boolean = false,
    val leftPart: LeftPart? = null,
    val middlePart: TileState? = null,
    val rightPart: RightPart? = null,
    val clickListener: (() -> Unit)? = null,
) : TileState {

    companion object {

        fun Data(
            hasBadge: Boolean = false,
            leftPart: LeftPart? = null,
            middlePart: TileState? = null,
            rightPart: RightPart? = null,
            clickListener: (() -> Unit)? = null,
        ) = CellTileState(
            hasBadge = hasBadge,
            leftPart = leftPart,
            middlePart = middlePart,
            rightPart = rightPart,
            clickListener = clickListener,
        )

        fun Shimmer(
            leftPart: LeftPart.Shimmer? = null,
            middlePart: MiddlePart.Shimmer? = null,
            rightPart: RightPart.Shimmer? = null,
        ) = CellTileState(
            leftPart = leftPart,
            middlePart = middlePart,
            rightPart = rightPart,
        )

        fun Error(
            data: MessageBannerState,
        ) = CellTileState(middlePart = data)

        fun Error(
            clickListener: () -> Unit,
        ) = CellTileState(middlePart = MessageBannerState(onClick = clickListener))
    }

    @Composable
    override fun Content(modifier: Modifier) {
        CellTile(modifier, this)
    }
}

object CellTileConfig {

    val HorizontalPadding = SpecificValues.default_padding
    val RipplePadding = 0.dp
    val VerticalPadding = 12.dp
    val VerticalHeaderPadding = 8.dp
    val BadgePadding = 5.dp

    val SmallIconSize = 20.dp
    val AvatarSize = 48.dp
    val IconSize = 35.dp
    val BetweenPadding = 12.dp

    val AvatarDividerPadding = AvatarSize + BetweenPadding + HorizontalPadding
    val StartDividerPadding = IconSize + BetweenPadding + HorizontalPadding
    val SmallStartDividerPadding = SmallIconSize + BetweenPadding + HorizontalPadding
}

@Composable
fun CellTile(
    modifier: Modifier = Modifier,
    data: CellTileState,
) {
    CellTileContainer(
        modifier = modifier,
        hasBadge = data.hasBadge,
        clickListener = data.clickListener,
    ) {
        data.leftPart?.Content(Modifier)
        data.middlePart?.Content(Modifier.weight(1f))
        data.rightPart?.Content(Modifier)
    }
}

@Composable
fun CellTileContainer(
    modifier: Modifier = Modifier,
    verticalPadding: Dp = CellTileConfig.VerticalPadding,
    clickListener: (() -> Unit)? = null,
    hasBadge: Boolean = false,
    content: @Composable RowScope.() -> Unit,
) {
    val ripplePadding = CellTileConfig.RipplePadding
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(ripplePadding)
            .clip(AppTheme.shapes.small)
            .doOnClick(enable = clickListener != null, onClick = clickListener)
            .padding(vertical = verticalPadding - ripplePadding)
            .padding(end = CellTileConfig.HorizontalPadding - ripplePadding)
            .addIf(!hasBadge) { padding(start = CellTileConfig.HorizontalPadding - ripplePadding) },
    ) {
        if (hasBadge) Badge(
            modifier = Modifier.padding(
                end = CellTileConfig.BadgePadding,
                start = CellTileConfig.BadgePadding - ripplePadding,
            ),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CellTileConfig.BetweenPadding),
            content = content,
        )
    }
}