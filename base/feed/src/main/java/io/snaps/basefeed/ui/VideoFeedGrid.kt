package io.snaps.basefeed.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.snaps.basefeed.domain.VideoClipModel
import io.snaps.corecommon.container.IconValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.imageValue
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
            /*key = { _, item -> item.key },*/
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
    uiState.emptyState?.Content(modifier = Modifier.fillMaxSize())
    uiState.errorState?.Content(modifier = Modifier.fillMaxSize())
}

@Composable
private fun Item(
    item: VideoClipModel,
    onClick: () -> Unit,
) {
    ItemContainer(
        onClick = onClick,
    ) {
        item.thumbnail?.let {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = it.imageValue().get(),
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        }
        @Composable
        fun Column(icon: IconValue, value: String) {
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    painter = icon.get(),
                    contentDescription = null,
                    tint = AppTheme.specificColorScheme.white,
                    modifier = Modifier.size(12.dp),
                )
                Text(
                    text = value,
                    color = AppTheme.specificColorScheme.white,
                    style = AppTheme.specificTypography.bodySmall,
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Column(icon = AppTheme.specificIcons.favorite, value = item.likeCount.toString())
            Spacer(modifier = Modifier.width(20.dp))
            Column(icon = AppTheme.specificIcons.eye, value = item.viewCount.toString())
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