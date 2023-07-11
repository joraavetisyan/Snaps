package io.snaps.basefeed.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.snaps.basefeed.data.UploadStatusSource
import io.snaps.basefeed.data.model.VideoStatus
import io.snaps.basefeed.domain.VideoClipModel
import io.snaps.corecommon.container.IconValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.addIf
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.doOnClick
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.other.ShimmerTile
import io.snaps.coreuicompose.uikit.scroll.ScrollEndDetectLazyVerticalGrid
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.colors
import kotlinx.coroutines.flow.Flow

@Composable
fun VideoFeedGrid(
    columnCount: Int,
    detectThreshold: Int = columnCount * 2,
    isShowStatus: Boolean = false,
    uploadState: ((videoId: Uuid) -> Flow<UploadStatusSource.State>?)? = null,
    onRetryUploadClicked: (videoId: Uuid) -> Unit = {},
    uiState: VideoFeedUiState,
    onClick: (Int) -> Unit,
    contentPadding: PaddingValues,
) {
    val lazyGridState = rememberLazyGridState()
    ScrollEndDetectLazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        state = lazyGridState,
        columns = GridCells.Fixed(columnCount),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        onScrollEndDetected = uiState.onListEndReaching,
        detectThreshold = detectThreshold,
        contentPadding = contentPadding,
    ) {
        itemsIndexed(
            items = uiState.items,
            /*key = { _, item -> item.key },*/
        ) { index, it ->
            when (it) {
                is VideoClipUiState.Data -> Item(
                    item = it.clip,
                    isShowStatus = isShowStatus,
                    uploadState = uploadState,
                    onRetryUploadClicked = onRetryUploadClicked,
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
    isShowStatus: Boolean,
    uploadState: ((videoId: Uuid) -> Flow<UploadStatusSource.State>?)? = null,
    onRetryUploadClicked: (videoId: Uuid) -> Unit = {},
    onClick: () -> Unit,
) {
    ItemContainer(
        onClick = onClick,
    ) {
        Thumbnail(modifier = Modifier.weight(1f), item = item)
        if (isShowStatus) {
            StatusMessage(status = item.status)
            if (item.internalId != null && uploadState != null) {
                when (val state = uploadState(item.internalId)?.collectAsState(null)?.value) {
                    is UploadStatusSource.State.Error -> ReloadButton(
                        onClick = { onRetryUploadClicked(item.internalId) },
                    )

                    is UploadStatusSource.State.Progress -> UploadProgress(
                        uploadingProgress = state.progress,
                    )

                    is UploadStatusSource.State.Success,
                    null -> Unit
                }
            }
        }
    }
}

@Composable
private fun Thumbnail(
    modifier: Modifier,
    item: VideoClipModel,
) {
    Box(
        modifier
            .background(
                color = AppTheme.specificColorScheme.black_10.copy(alpha = 0.1f),
            )
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
        fun Info(icon: IconValue, value: String) {
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
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Info(icon = AppTheme.specificIcons.favorite, value = item.likeCount.toString())
            Spacer(modifier = Modifier.width(8.dp))
            Info(icon = AppTheme.specificIcons.eye, value = item.viewCount.toString())
        }
    }
}

@Composable
private fun StatusMessage(status: VideoStatus?) {
    Log.d("CheckForStatus", status.toString())
    if (status == null || status == VideoStatus.Approved) return
    val color = colors {
        when (status) {
            VideoStatus.Rejected -> uiSystemRed
            VideoStatus.InReview -> uiSystemYellow
            else -> uiSystemGreen
        }
    }
    Block(
        text = when (status) {
            VideoStatus.Rejected -> StringKey.MessageVideoRejected.textValue()
            VideoStatus.InReview -> StringKey.MessageVideoInReview.textValue()
            else -> "".textValue()
        },
        textColor = color,
        backgroundColor = color.copy(alpha = 0.1f),
    )
}

@Composable
private fun Block(
    text: TextValue,
    textColor: Color,
    backgroundColor: Color,
    onClick: (() -> Unit)? = null,
) {
    Text(
        text = text.get(),
        color = textColor,
        style = AppTheme.specificTypography.bodySmall,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(top = 4.dp)
            .fillMaxWidth()
            .clip(shape = AppTheme.shapes.medium)
            .background(color = backgroundColor)
            .doOnClick(onClick = onClick)
            .padding(8.dp),
        maxLines = 1,
    )
}

@Composable
private fun ReloadButton(
    onClick: () -> Unit,
) {
    Block(
        text = StringKey.ActionReload.textValue(),
        textColor = AppTheme.specificColorScheme.white,
        backgroundColor = AppTheme.specificColorScheme.uiSystemRed,
        onClick = onClick,
    )
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
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .addIf(onClick != null) {
                defaultTileRipple(
                    onClick = onClick,
                    padding = 0.dp,
                    shape = RectangleShape
                )
            }
            .aspectRatio(1f / 2f),
    ) {
        content()
    }
}
