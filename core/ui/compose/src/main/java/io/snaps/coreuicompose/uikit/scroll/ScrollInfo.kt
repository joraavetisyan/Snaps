package io.snaps.coreuicompose.uikit.scroll

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.fold

const val DETECT_THRESHOLD = 5

class ScrollInfo(
    val isReachingEnd: Boolean,
    val totalItemsCount: Int,
) {

    override fun equals(other: Any?): Boolean =
        this.isReachingEnd == (other as ScrollInfo).isReachingEnd

    override fun hashCode(): Int = isReachingEnd.hashCode()
}

@Composable
fun DetectScroll(
    scrollInfo: State<ScrollInfo>,
    onScrollEndDetected: (() -> Unit)?,
) {
    val onScrollEndDetectedRemembered by rememberUpdatedState(onScrollEndDetected)

    LaunchedEffect(scrollInfo) {
        snapshotFlow { scrollInfo.value }.fold(0) { previousTotalItemsCount, currentScrollInfo ->
            if (currentScrollInfo.isReachingEnd && currentScrollInfo.totalItemsCount > previousTotalItemsCount) {
                onScrollEndDetectedRemembered?.invoke()
            }
            if (currentScrollInfo.isReachingEnd) {
                currentScrollInfo.totalItemsCount
            } else {
                previousTotalItemsCount
            }
        }
    }
}