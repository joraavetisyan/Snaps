package io.snaps.coreuicompose.uikit.scroll

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.snaps.coreuicompose.tools.add

@Composable
fun ScrollEndDetectLazyVerticalGrid(
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(12.dp).add(padding = PaddingValues(bottom = 100.dp)),
    horizontalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(4.dp),
    verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(4.dp),
    detectThreshold: Int = DETECT_THRESHOLD,
    columns: GridCells,
    onScrollEndDetected: (() -> Unit)?,
    content: LazyGridScope.() -> Unit,
) {
    val scrollInfo = remember {
        derivedStateOf {
            val lastVisibleItem = state.layoutInfo.visibleItemsInfo.lastOrNull()
            ScrollInfo(
                isReachingEnd = lastVisibleItem != null &&
                        lastVisibleItem.index + 1 >= state.layoutInfo.totalItemsCount - detectThreshold,
                totalItemsCount = state.layoutInfo.totalItemsCount,
            )
        }
    }

    DetectScroll(scrollInfo, onScrollEndDetected)

    LazyVerticalGrid(
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        content = content,
        columns = columns,
    )
}