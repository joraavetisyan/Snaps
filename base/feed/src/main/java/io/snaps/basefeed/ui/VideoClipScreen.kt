package io.snaps.basefeed.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.snaps.basefeed.domain.VideoClipModel
import io.snaps.baseplayer.ui.VideoPlayer
import io.snaps.corecommon.R
import io.snaps.corecommon.container.IconValue
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.startShareLinkIntent
import io.snaps.corecommon.ext.toCompactDecimalFormat
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.strings.StringKey
import io.snaps.corenavigation.base.openUrl
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.LocalBottomNavigationHeight
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.doOnClick
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetBottom
import io.snaps.coreuicompose.uikit.bottomsheetdialog.ActionsBottomDialog
import io.snaps.coreuicompose.uikit.bottomsheetdialog.ModalBottomSheetTargetStateListener
import io.snaps.coreuicompose.uikit.button.SimpleChip
import io.snaps.coreuicompose.uikit.button.SimpleChipConfig
import io.snaps.coreuicompose.uikit.dialog.SimpleConfirmDialogUi
import io.snaps.coreuicompose.uikit.other.Progress
import io.snaps.coreuicompose.uikit.other.ShimmerTileCircle
import io.snaps.coreuicompose.uikit.scroll.DetectScroll
import io.snaps.coreuicompose.uikit.scroll.ScrollInfo
import io.snaps.coreuicompose.uikit.status.FullScreenLoaderUi
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.colors
import io.snaps.coreuitheme.compose.icons
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val DETECT_THRESHOLD = 3

@Composable
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class,
)
fun VideoClipScreen(
    viewModel: VideoFeedViewModel,
    onAuthorClicked: (Uuid) -> Unit,
    onCreateVideoClicked: (() -> Unit)? = null,
    onCloseScreen: (() -> Unit)? = null,
    content: @Composable ((BoxScope.(PaddingValues) -> Unit))? = null,
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val pagerState = rememberPagerState(initialPage = viewModel.startPosition)
    val currentPage by remember(pagerState) { derivedStateOf { pagerState.currentPage } }
    LaunchedEffect(currentPage) { viewModel.onScrolledToPosition(currentPage) }

    fun hideKeyboard() {
        focusManager.clearFocus()
        keyboardController?.hide()
    }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    val commentInputSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )

    ModalBottomSheetTargetStateListener(
        sheetState = sheetState,
        onStateToChange = viewModel::onBottomDialogStateChange,
    )

    LaunchedEffect(Unit) {
        // drop initial value as FocusRequester may not been initialized at this time and freeFocus could throw
        snapshotFlow { commentInputSheetState.currentValue }.drop(1).collect {
            if (it == ModalBottomSheetValue.Hidden) {
                hideKeyboard()
                viewModel.onCommentInputBottomSheetHidden()
            }
        }
    }

    fun ModalBottomSheetState.showSheet() = coroutineScope.launch { show() }
    fun ModalBottomSheetState.hideSheet() = coroutineScope.launch { hide() }

    viewModel.command.collectAsCommand {
        when (it) {
            VideoFeedViewModel.Command.ShowBottomDialog -> sheetState.showSheet()
            VideoFeedViewModel.Command.HideBottomDialog -> sheetState.hideSheet()
            VideoFeedViewModel.Command.ShowCommentInputBottomDialog -> {
                commentInputSheetState.showSheet()
                focusRequester.requestFocus()
            }

            VideoFeedViewModel.Command.CloseScreen -> onCloseScreen?.invoke()
            VideoFeedViewModel.Command.HideCommentInputBottomDialog -> commentInputSheetState.hideSheet()
            is VideoFeedViewModel.Command.ScrollToPosition -> pagerState.scrollToPage(it.position)
            is VideoFeedViewModel.Command.OpenProfileScreen -> onAuthorClicked(it.userId)
            is VideoFeedViewModel.Command.ShareVideoClipLink -> context.startShareLinkIntent(it.link)
        }
    }

    LaunchedEffect(key1 = pagerState) {
        snapshotFlow { pagerState.currentPage }.distinctUntilChanged().collect { page ->
            pagerState.animateScrollToPage(page)
        }
    }

    BackHandler(enabled = sheetState.isVisible) {
        if (commentInputSheetState.isVisible) {
            if (focusRequester.freeFocus()) {
                hideKeyboard()
            } else {
                commentInputSheetState.hideSheet()
            }
        } else {
            sheetState.hideSheet()
        }
    }

    ModalBottomSheetLayout(
        modifier = Modifier.background(AppTheme.specificColorScheme.black),
        sheetState = commentInputSheetState,
        sheetContent = {
            CommentInput(
                focusRequester = focusRequester,
                profileImage = uiState.profileAvatar,
                value = uiState.comment,
                onValueChange = viewModel::onCommentChanged,
                isSendEnabled = uiState.isCommentSendEnabled,
                isEditable = true,
                onEmojiClick = viewModel::onEmojiClicked,
                onInputClick = {},
                onSendClick = viewModel::onCommentSendClick,
            )
        },
    ) {
        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = {
                when (uiState.bottomDialog) {
                    VideoFeedViewModel.BottomDialog.Comments -> CommentsScreen(
                        uiState = uiState,
                        onCommentInputClicked = viewModel::onCommentInputClick,
                        onCloseClicked = sheetState::hideSheet,
                        onReplyClicked = commentInputSheetState::showSheet,
                        onEmojiClicked = viewModel::onEmojiClicked,
                    )

                    VideoFeedViewModel.BottomDialog.MoreActions -> ActionsBottomDialog(
                        title = StringKey.VideoClipTitleAction.textValue(),
                        actions = uiState.actions,
                    )
                }
            },
        ) {
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

            Scaffold(
                modifier = Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .inset(insetBottom()),
            ) { paddingValues ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    ScrollDetector(
                        pageCount = uiState.videoFeedUiState.dataSize,
                        pagerState = pagerState,
                        onListEndReaching = uiState.videoFeedUiState.onListEndReaching,
                    )

                    VerticalPager(
                        pageCount = uiState.videoFeedUiState.items.size,
                        state = pagerState,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        key = {
                            // todo
                            try {
                                uiState.videoFeedUiState.items[it].key
                            } catch (e: IndexOutOfBoundsException) {
                                e
                            }
                        },
                    ) { index ->
                        when (val item = uiState.videoFeedUiState.items[index]) {
                            is VideoClipUiState.Data -> {
                                VideoClip(
                                    pagerState = pagerState,
                                    index = index,
                                    item = item,
                                    uiState = uiState,
                                    onMuteClicked = viewModel::onMuteClicked,
                                    onProgressChanged = {
                                        viewModel.onVideoClipWatchProgressed(clipModel = it)
                                    },
                                    onAuthorClicked = viewModel::onAuthorClicked,
                                    onLikeClicked = viewModel::onLikeClicked,
                                    onDoubleLikeClicked = viewModel::onDoubleLikeClicked,
                                    onCommentClicked = viewModel::onCommentClicked,
                                    onShareClicked = viewModel::onShareClicked,
                                    onMoreClicked = viewModel::onMoreClicked,
                                    onCreateVideoClicked = onCreateVideoClicked,
                                    onSubscribeClicked = viewModel::onSubscribeClicked,
                                    onVideoClipStartedPlaying = viewModel::onVideoClipStartedPlaying,
                                )
                            }

                            is VideoClipUiState.Shimmer -> FullScreenLoaderUi(
                                isLoading = true,
                                backgroundColor = AppTheme.specificColorScheme.black,
                            )
                        }
                    }

                    uiState.videoFeedUiState.emptyState?.Content(Modifier.align(Alignment.Center))
                    uiState.videoFeedUiState.errorState?.Content(Modifier.align(Alignment.Center))

                    content?.invoke(this, paddingValues)
                }
            }
        }
    }
    uiState.dialog?.let {
        when (it) {
            VideoFeedViewModel.Dialog.ConfirmDeleteVideo -> SimpleConfirmDialogUi(
                title = StringKey.VideoClipDialogConfirmDeleteMessage.textValue(),
                confirmButtonText = StringKey.ActionDelete.textValue(),
                onDismissRequest = viewModel::onDeleteDismissed,
                onConfirmRequest = viewModel::onDeleteConfirmed,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ScrollDetector(
    pageCount: Int,
    pagerState: PagerState,
    onListEndReaching: (() -> Unit)?,
) {
    val pageCountRemembered by rememberUpdatedState(pageCount)
    val scrollInfo = remember(pagerState) {
        derivedStateOf {
            val currentPage = pagerState.currentPage
            val isReachingEnd =
                pageCountRemembered > 0 && currentPage + 1 >= pageCountRemembered - DETECT_THRESHOLD
            ScrollInfo(
                isReachingEnd = isReachingEnd,
                totalItemsCount = pageCountRemembered,
            )
        }
    }

    DetectScroll(scrollInfo, onListEndReaching)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun VideoClip(
    pagerState: PagerState,
    index: Int,
    item: VideoClipUiState.Data,
    uiState: VideoFeedViewModel.UiState,
    onMuteClicked: (Boolean) -> Unit,
    onProgressChanged: (VideoClipModel) -> Unit,
    onAuthorClicked: (VideoClipModel) -> Unit,
    onLikeClicked: (VideoClipModel) -> Unit,
    onDoubleLikeClicked: (VideoClipModel) -> Unit,
    onCommentClicked: (VideoClipModel) -> Unit,
    onShareClicked: (VideoClipModel) -> Unit,
    onMoreClicked: () -> Unit,
    onCreateVideoClicked: (() -> Unit)?,
    onSubscribeClicked: () -> Unit,
    onVideoClipStartedPlaying: (VideoClipModel, Float, Float) -> Unit,
) {
    val shouldPlay by remember(pagerState) {
        derivedStateOf {
            val a =
                abs(pagerState.currentPageOffsetFraction) < 0.5f && pagerState.currentPage == index
            val b =
                abs(pagerState.currentPageOffsetFraction) > 0.5f && pagerState.currentPage == index
            a || b
        }
    }


    var progress by remember(item.clip.id) { mutableStateOf(0f) }
    var currentPage by remember { mutableStateOf(pagerState.currentPage) }
    var duration by remember { mutableStateOf(0f) }

    LaunchedEffect(key1 = pagerState.currentPage) {
        if (pagerState.currentPage != currentPage && progress > 0) {
            onVideoClipStartedPlaying(
                item.clip,
                duration / 1000,
                progress * duration / 1000
            )

            currentPage = pagerState.currentPage
        }
    }

    VideoPlayer(
        networkUrl = item.clip.url,
        shouldPlay = shouldPlay,
        isLiked = item.clip.isLiked,
        isMuted = uiState.isMuted,
        onMuted = onMuteClicked,
        isScrolling = pagerState.isScrollInProgress,
        onLiked = { onDoubleLikeClicked(item.clip) },
        onProgressChanged = { progress = it },
        onDurationReady = { duration = it },
        progressPollFrequencyInMillis = 10L,
        performAtPosition = { onProgressChanged(item.clip) },
        performPosition = 0.7f,
    )

    VideoClipItems(
        videoClipModel = item.clip,
        isMoreIconVisible = uiState.actions.isNotEmpty(),
        isSubscribeButtonVisible = uiState.isSubscribeButtonVisible,
        isSubscribed = uiState.isSubscribed,
        authorProfileAvatar = uiState.authorProfileAvatar,
        authorName = uiState.authorName,
        progress = progress,
        onAuthorClicked = onAuthorClicked,
        onLikeClicked = onLikeClicked,
        onCommentClicked = onCommentClicked,
        onShareClicked = onShareClicked,
        onMoreClicked = onMoreClicked,
        onCreateVideoClicked = onCreateVideoClicked,
        onSubscribeClicked = onSubscribeClicked,
    )
}

@Composable
private fun VideoClipItems(
    modifier: Modifier = Modifier,
    videoClipModel: VideoClipModel,
    isMoreIconVisible: Boolean,
    isSubscribeButtonVisible: Boolean,
    isSubscribed: Boolean,
    authorProfileAvatar: ImageValue?,
    authorName: String,
    progress: Float,
    onAuthorClicked: (VideoClipModel) -> Unit,
    onLikeClicked: (VideoClipModel) -> Unit,
    onCommentClicked: (VideoClipModel) -> Unit,
    onShareClicked: (VideoClipModel) -> Unit,
    onMoreClicked: () -> Unit,
    onCreateVideoClicked: (() -> Unit)?,
    onSubscribeClicked: () -> Unit,
) {
    Box(modifier = modifier) {
        // Darkening the lower part, so the info items are more contrasted
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.5f)))
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = LocalBottomNavigationHeight.current)
        ) {
            VideoClipInfoItems(
                clipModel = videoClipModel,
                isMoreIconVisible = isMoreIconVisible,
                isSubscribeButtonVisible = isSubscribeButtonVisible,
                isSubscribed = isSubscribed,
                authorProfileAvatar = authorProfileAvatar,
                authorName = authorName,
                onAuthorClicked = onAuthorClicked,
                onLikeClicked = onLikeClicked,
                onCommentClicked = onCommentClicked,
                onShareClicked = onShareClicked,
                onMoreClicked = onMoreClicked,
                onCreateVideoClicked = onCreateVideoClicked,
                onSubscribeClicked = onSubscribeClicked,
            )
            Progress(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                progress = progress,
                isDashed = false,
                backColor = AppTheme.specificColorScheme.white_40,
                fillColor = AppTheme.specificColorScheme.white,
                height = 2.dp,
            )
        }
    }
}

@Composable
private fun VideoClipInfoItems(
    clipModel: VideoClipModel,
    isMoreIconVisible: Boolean,
    isSubscribeButtonVisible: Boolean,
    isSubscribed: Boolean,
    authorProfileAvatar: ImageValue?,
    authorName: String,
    onAuthorClicked: (VideoClipModel) -> Unit,
    onLikeClicked: (VideoClipModel) -> Unit,
    onCommentClicked: (VideoClipModel) -> Unit,
    onShareClicked: (VideoClipModel) -> Unit,
    onMoreClicked: () -> Unit,
    onCreateVideoClicked: (() -> Unit)?,
    onSubscribeClicked: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.Bottom,
    ) {
        VideoClipBottomItems(
            modifier = Modifier.fillMaxWidth(0.7f),
            clipModel = clipModel,
            authorName = authorName,
            isSubscribeButtonVisible = isSubscribeButtonVisible,
            isSubscribed = isSubscribed,
            onSubscribeClicked = onSubscribeClicked,
        )
        Spacer(modifier = Modifier.weight(1f))
        VideoClipEndItems(
            clipModel = clipModel,
            isMoreIconVisible = isMoreIconVisible,
            authorProfileAvatar = authorProfileAvatar,
            onAuthorClicked = onAuthorClicked,
            onLikeClicked = onLikeClicked,
            onCommentClicked = onCommentClicked,
            onShareClicked = onShareClicked,
            onMoreClicked = onMoreClicked,
            onCreateVideoClicked = onCreateVideoClicked,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VideoClipBottomItems(
    modifier: Modifier = Modifier,
    isSubscribeButtonVisible: Boolean,
    isSubscribed: Boolean,
    clipModel: VideoClipModel,
    authorName: String,
    onSubscribeClicked: () -> Unit,
) {
    var isDescriptionExpanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .padding(start = 16.dp, bottom = 16.dp),
        contentAlignment = Alignment.BottomStart,
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            if (clipModel.isSponsored) {
                Text(
                    text = StringKey.FieldSponsored.textValue().get(),
                    color = AppTheme.specificColorScheme.white,
                    style = AppTheme.specificTypography.bodySmall,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .clip(shape = AppTheme.shapes.medium)
                        .background(color = AppTheme.specificColorScheme.black_50)
                        .padding(8.dp),
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = authorName,
                    style = AppTheme.specificTypography.bodyLarge,
                    maxLines = 1,
                    color = Color.White,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (isSubscribeButtonVisible && !clipModel.isSponsored) {
                    SimpleChip(
                        selected = isSubscribed,
                        onClick = onSubscribeClicked,
                        label = (if (isSubscribed) StringKey.SubsActionFollowing else StringKey.SubsActionFollow).textValue(),
                        colors = SimpleChipConfig.greyColor(),
                        leadingIcon = AppTheme.specificIcons.checkBox.toImageValue()
                            .takeIf { isSubscribed },
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            val scrollState = rememberScrollState()
            val interactionSource = remember { MutableInteractionSource() }
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Text(
                    text = clipModel.title,
                    style = AppTheme.specificTypography.bodySmall,
                    maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 2,
                    color = AppTheme.specificColorScheme.white,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                        ) { isDescriptionExpanded = !isDescriptionExpanded }
                        .animateContentSize(),
                )
            }
            val context = LocalContext.current
            if (clipModel.learnMoreLink != null) {
                Spacer(modifier = Modifier.height(8.dp))
                // todo use default ui element (add if needed)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = AppTheme.shapes.medium)
                        .background(color = AppTheme.specificColorScheme.black_50)
                        .doOnClick(onClick = { context.openUrl(clipModel.learnMoreLink) })
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = StringKey.ActionLearnMore.textValue().get(),
                        color = AppTheme.specificColorScheme.white,
                        style = AppTheme.specificTypography.bodySmall,
                        textAlign = TextAlign.Start,
                        maxLines = 2,
                    )
                    Icon(
                        painter = AppTheme.specificIcons.forward.get(),
                        contentDescription = null,
                        tint = AppTheme.specificColorScheme.white,
                    )
                }
            }
        }
    }
}

@Composable
private fun VideoClipEndItems(
    clipModel: VideoClipModel,
    isMoreIconVisible: Boolean,
    authorProfileAvatar: ImageValue?,
    onAuthorClicked: (VideoClipModel) -> Unit,
    onLikeClicked: (VideoClipModel) -> Unit,
    onCommentClicked: (VideoClipModel) -> Unit,
    onShareClicked: (VideoClipModel) -> Unit,
    onMoreClicked: () -> Unit,
    onCreateVideoClicked: (() -> Unit)?,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 16.dp),
    ) {
        Box(
            modifier = Modifier.defaultTileRipple(
                shape = CircleShape,
                onClick = if (clipModel.isSponsored) null else {
                    { onAuthorClicked(clipModel) }
                },
            )
        ) {
            Card(
                shape = CircleShape,
                modifier = Modifier.padding(10.dp),
            ) {
                if (authorProfileAvatar != null) {
                    Image(
                        painter = authorProfileAvatar.get(),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    ShimmerTileCircle(size = 60.dp)
                }
            }
            Icon(
                painter = AppTheme.specificIcons.addCircled.get(),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.BottomCenter),
            )
        }

        TextedIcon(
            icon = icons { if (clipModel.isLiked) favorite else favoriteBorder },
            text = if (clipModel.isSponsored) null else clipModel.likeCount.toCompactDecimalFormat(),
            tint = colors { if (clipModel.isLiked) red else white },
            onIconClicked = if (clipModel.isSponsored) null else {
                { onLikeClicked(clipModel) }
            },
        )

        TextedIcon(
            icon = AppTheme.specificIcons.comment,
            text = if (clipModel.isSponsored) null else clipModel.commentCount.toCompactDecimalFormat(),
            onIconClicked = if (clipModel.isSponsored) null else {
                { onCommentClicked(clipModel) }
            },
        )

        IconButton(onClick = { onShareClicked(clipModel) }) {
            Icon(
                painter = AppTheme.specificIcons.share.get(),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp),
            )
        }
        if (isMoreIconVisible) {
            IconButton(onClick = onMoreClicked) {
                Icon(
                    painter = AppTheme.specificIcons.moreVert.get(),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp),
                )
            }
        }
        if (onCreateVideoClicked != null) {
            IconButton(onClick = { onCreateVideoClicked() }) {
                Icon(
                    painter = R.drawable.img_create.imageValue().get(),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(60.dp),
                )
            }
        }
    }
}

@Composable
private fun TextedIcon(
    modifier: Modifier = Modifier,
    icon: IconValue,
    text: String?,
    tint: Color = AppTheme.specificColorScheme.white,
    contentDescription: String? = null,
    onIconClicked: (() -> Unit)? = null,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconButton(onClick = { onIconClicked?.invoke() }, enabled = onIconClicked != null) {
            Icon(
                painter = icon.get(),
                contentDescription = contentDescription,
                tint = tint,
                modifier = modifier.size(36.dp),
            )
        }
        if (text != null) Text(
            text = text,
            color = AppTheme.specificColorScheme.white,
        )
    }
}