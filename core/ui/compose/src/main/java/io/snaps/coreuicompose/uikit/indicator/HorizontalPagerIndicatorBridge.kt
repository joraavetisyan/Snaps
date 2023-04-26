package io.snaps.coreuicompose.uikit.indicator

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable

@Suppress("NOTHING_TO_INLINE")
@OptIn(ExperimentalFoundationApi::class)
@Composable
inline fun HorizontalPagerIndicatorBridge(
    pagerState: androidx.compose.foundation.pager.PagerState,
    pageCount: Int,
) {
    com.google.accompanist.pager.HorizontalPagerIndicator(
        pagerState = pagerState,
        pageCount = pageCount,
    )
}