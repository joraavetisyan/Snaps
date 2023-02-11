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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import io.snaps.baseplayer.domain.VideoClipModel
import io.snaps.baseplayer.ui.ReelPlayer
import io.snaps.corecommon.container.ImageValue
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.other.ShimmerTile
import io.snaps.coreuicompose.uikit.scroll.ScrollEndDetectLazyVerticalGrid
import io.snaps.coreuitheme.compose.AppTheme

@Composable
fun VideoFeedGrid(
    columnCount: Int,
    detectThreshold: Int = columnCount * 2,
    uiState: VideoFeedUiState,
) {
    val lazyGridState = rememberLazyGridState()
    val firstVisibleItemIndex by remember {
        derivedStateOf { lazyGridState.firstVisibleItemScrollOffset }
    }
    val firstFullyVisibleItemIndex: Int? by remember {
        derivedStateOf {
            val layoutInfo = lazyGridState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty()) {
                emptyList()
            } else {
                val fullyVisibleItemsInfo = visibleItemsInfo.toMutableList()
                val viewportHeight =
                    layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset
                repeat(columnCount) {
                    val lastItem = fullyVisibleItemsInfo.last()
                    if ((lastItem.offset.y + lastItem.size.height) > viewportHeight) {
                        fullyVisibleItemsInfo.removeLast()
                    }
                }
                repeat(columnCount) {
                    val firstItemIfLeft = fullyVisibleItemsInfo.firstOrNull()
                    if (firstItemIfLeft != null && firstItemIfLeft.offset.y < layoutInfo.viewportStartOffset) {
                        fullyVisibleItemsInfo.removeFirst()
                    }
                }
                fullyVisibleItemsInfo.map { it.index }
            }.firstOrNull()
        }
    }
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
                    shouldPlay = index == (firstFullyVisibleItemIndex ?: firstVisibleItemIndex),
                    isScrolling = lazyGridState.isScrollInProgress,
                )
                is VideoClipUiState.Shimmer -> ItemShimmer()
            }
        }
    }
}

@Composable
private fun Item(
    item: VideoClipModel,
    shouldPlay: Boolean,
    isScrolling: Boolean,
) {
    ItemContainer {
        if (shouldPlay) {
            ReelPlayer(
                videoClipUrl = item.url,
                shouldPlay = !isScrolling,
            )
        } else {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = ImageValue.Url(item.thumbnail).get(),
                contentDescription = null,
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 4.dp, vertical = 8.dp),
        ) {
            Icon(AppTheme.specificIcons.play.get(), null, tint = AppTheme.specificColorScheme.white)
            Text(item.likeCount.toString(), color = AppTheme.specificColorScheme.white)
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
private fun ItemContainer(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
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