package io.snaps.basefeed.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import io.snaps.baseplayer.domain.VideoClipModel
import io.snaps.corecommon.container.ImageValue
import io.snaps.coreuicompose.tools.addIf
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.other.ShimmerTile
import io.snaps.coreuicompose.uikit.scroll.ScrollEndDetectLazyVerticalGrid
import io.snaps.coreuitheme.compose.AppTheme

@Composable
fun VideoFeedGrid(
    columnCount: Int,
    detectThreshold: Int = columnCount * 2,
    uiState: VideoFeedUiState,
    onClick: (Int) -> Unit,
) {
    val lazyGridState = rememberLazyGridState()
    ScrollEndDetectLazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        state = lazyGridState,
        columns = GridCells.Fixed(columnCount),
        onScrollEndDetected = uiState.onListEndReaching,
        detectThreshold = detectThreshold,
    ) {
        itemsIndexed(
            items = uiState.items,
            key = { _, item -> item.id },
        ) { index, it ->
            when (it) {
                is VideoClipUiState.Data -> Item(
                    item = it.clip,
                    onClick = { onClick(index) },
                )
                is VideoClipUiState.Shimmer -> ItemShimmer()
            }
        }
    }
}

@Composable
private fun Item(
    item: VideoClipModel,
    onClick: () -> Unit,
) {
    ItemContainer(
        onClick = onClick,
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = ImageValue.Url(item.thumbnail).get(),
            contentDescription = null,
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = AppTheme.specificIcons.play.get(),
                contentDescription = null,
                tint = AppTheme.specificColorScheme.white,
            )
            Text(
                text = item.likeCount.toString(),
                color = AppTheme.specificColorScheme.white,
                style = AppTheme.specificTypography.bodySmall,
            )
        }
    }
}

@Composable
private fun ItemShimmer() {
    ItemContainer {
        ShimmerTile(Modifier.fillMaxSize())
    }
}

@Composable
private fun ItemContainer(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .addIf(onClick != null) { defaultTileRipple(onClick = onClick) }
            .aspectRatio(177f / 222f)
            .shadow(elevation = 16.dp, shape = AppTheme.shapes.medium)
            .background(
                color = AppTheme.specificColorScheme.uiContentBg,
                shape = AppTheme.shapes.medium,
            ),
    ) {
        content()
    }
}