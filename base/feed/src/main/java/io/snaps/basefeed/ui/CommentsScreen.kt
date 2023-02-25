package io.snaps.basefeed.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.date.toStringValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.listtile.CellTile
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.LeftPart
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
import io.snaps.coreuicompose.uikit.scroll.ScrollEndDetectLazyColumn
import io.snaps.coreuitheme.compose.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(
    uiState: VideoFeedViewModel.UiState,
    onCommentInputClicked: () -> Unit,
    onCommentChanged: (TextFieldValue) -> Unit,
    onCloseClicked: () -> Unit,
    onReplyClicked: () -> Unit,
    onSendClicked: () -> Unit,
    onEmojiClicked: (String) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {},
        bottomBar = {
            CommentInput(
                profileImage = uiState.profileAvatar,
                value = uiState.comment,
                onValueChange = onCommentChanged,
                isEditable = false,
                onEmojiClick = { onEmojiClicked(it) },
                onInputClick = onCommentInputClicked,
                onSendClick = onSendClicked,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .inset(insetAllExcludeTop())
                .padding(12.dp),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = StringKey.CommentsTitle
                        .textValue(uiState.commentListSize.toString()).get(),
                    modifier = Modifier.align(Alignment.Center),
                    style = AppTheme.specificTypography.titleSmall
                )
                IconButton(
                    onClick = onCloseClicked,
                    modifier = Modifier.align(Alignment.CenterEnd),
                ) {
                    Icon(
                        painter = AppTheme.specificIcons.close.get(),
                        contentDescription = null,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            ScrollEndDetectLazyColumn(onScrollEndDetected = uiState.commentsUiState.onListEndReaching) {
                items(uiState.commentsUiState.items, key = { it.id }) {
                    when (it) {
                        is CommentUiState.Data -> Item(it, onReplyClicked)
                        is CommentUiState.Shimmer -> CellTile(
                            data = CellTileState.Data(
                                leftPart = LeftPart.Shimmer,
                                middlePart = MiddlePart.Shimmer(
                                    needHeaderLine = true,
                                    needValueLine = true,
                                    needAdditionalInfo = true,
                                ),
                                rightPart = RightPart.Shimmer(needRightCircle = true),
                            )
                        )
                        is CommentUiState.Progress -> Box(modifier = Modifier.fillMaxWidth()) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.Center),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Item(data: CommentUiState.Data, onReplyClicked: () -> Unit) {
    val item = data.item
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Card(
            shape = CircleShape,
        ) {
            Image(
                painter = item.ownerImage.get(),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                contentScale = ContentScale.Crop,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.ownerName,
                    style = AppTheme.specificTypography.bodySmall,
                    color = AppTheme.specificColorScheme.textSecondary,
                )
                if (item.isOwnerVerified) {
                    Image(
                        painter = AppTheme.specificIcons.verified.get(),
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        contentScale = ContentScale.Crop,
                    )
                }
                if (item.ownerTitle != null) {
                    Text(
                        text = " · " + item.ownerTitle,
                        color = AppTheme.specificColorScheme.uiSystemRed,
                        style = AppTheme.specificTypography.bodySmall,
                    )
                }
            }
            Text(
                text = item.text,
                style = AppTheme.specificTypography.bodySmall,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.createdDate.toStringValue(),
                    style = AppTheme.specificTypography.bodySmall,
                    color = AppTheme.specificColorScheme.textSecondary,
                )
                Text(
                    text = StringKey.ActionReply.textValue().get(),
                    style = AppTheme.specificTypography.bodySmall,
                    color = AppTheme.specificColorScheme.textSecondary,
                    modifier = Modifier.defaultTileRipple { onReplyClicked() }
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.End,
        ) {
            Image(
                painter = AppTheme.specificIcons.favoriteBorder.get(),
                contentDescription = null,
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop,
            )
            Text(
                text = item.likes.toString(),
                style = AppTheme.specificTypography.bodySmall,
                color = AppTheme.specificColorScheme.textSecondary,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
}