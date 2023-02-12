package io.snaps.featurefeed.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerScope
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import io.snaps.basefeed.ui.VideoClipUiState
import io.snaps.baseplayer.domain.VideoClipModel
import io.snaps.baseplayer.ui.ReelPlayer
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.ui.MainHeader
import io.snaps.baseprofile.ui.MainHeaderState
import io.snaps.corecommon.container.IconValue
import io.snaps.corecommon.container.ImageValue
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.scroll.DetectScroll
import io.snaps.coreuicompose.uikit.scroll.ScrollInfo
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featurefeed.ScreenNavigator
import io.snaps.featurefeed.presentation.viewmodel.VideoFeedViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val DETECT_THRESHOLD = 1

@OptIn(
    ExperimentalPagerApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class,
)
@Composable
fun VideoFeedScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<VideoFeedViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val mainHeaderUiState by viewModel.headerUiState.collectAsState()

    viewModel.headerCommand.collectAsCommand {
        when (it) {
            MainHeaderHandler.Command.OpenProfileScreen -> router.toProfileScreen()
            MainHeaderHandler.Command.OpenWalletScreen -> router.toWalletScreen()
        }
    }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    fun hideKeyboard() {
        focusRequester.freeFocus()
        keyboardController?.hide()
    }

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    val commentInputSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )

    LaunchedEffect(Unit) {
        snapshotFlow { sheetState.currentValue }.collect {
            if (it == ModalBottomSheetValue.Hidden) {
                viewModel.onBottomSheetHidden()
            }
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { commentInputSheetState.currentValue }.collect {
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
            VideoFeedViewModel.Command.HideCommentInputBottomDialog -> commentInputSheetState.hideSheet()
        }
    }

    val pagerState = rememberPagerState()

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

    VideoFeedScreen(
        focusRequester = focusRequester,
        sheetState = sheetState,
        commentInputSheetState = commentInputSheetState,
        pagerState = pagerState,
        uiState = uiState,
        mainHeaderState = mainHeaderUiState.value,
        onMuteClicked = viewModel::onMuteClicked,
        onAuthorClicked = viewModel::onAuthorClicked,
        onLikeClicked = viewModel::onLikeClicked,
        onCommentClicked = viewModel::onCommentClicked,
        onCommentChanged = viewModel::onCommentChanged,
        onCommentInputClick = viewModel::onCommentInputClick,
        onCommentSendClick = viewModel::onCommentSendClick,
        onEmojiClicked = viewModel::onEmojiClicked,
        onCommentsCloseClicked = sheetState::hideSheet,
        onReplyClicked = commentInputSheetState::showSheet,
        onShareClicked = viewModel::onShareClicked,
    )
}

@Composable
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
private fun VideoFeedScreen(
    focusRequester: FocusRequester,
    sheetState: ModalBottomSheetState,
    commentInputSheetState: ModalBottomSheetState,
    pagerState: PagerState,
    uiState: VideoFeedViewModel.UiState,
    mainHeaderState: MainHeaderState,
    onMuteClicked: (Boolean) -> Unit,
    onAuthorClicked: (VideoClipModel) -> Unit,
    onLikeClicked: (VideoClipModel) -> Unit,
    onCommentClicked: (VideoClipModel) -> Unit,
    onCommentsCloseClicked: () -> Unit,
    onCommentChanged: (TextFieldValue) -> Unit,
    onCommentInputClick: () -> Unit,
    onCommentSendClick: () -> Unit,
    onEmojiClicked: (String) -> Unit,
    onReplyClicked: () -> Unit,
    onShareClicked: (VideoClipModel) -> Unit,
) {
    ModalBottomSheetLayout(sheetState = commentInputSheetState, sheetContent = {
        CommentInput(
            focusRequester = focusRequester,
            profileImage = uiState.profileAvatar,
            value = uiState.comment,
            onValueChange = onCommentChanged,
            isEditable = true,
            onEmojiClick = onEmojiClicked,
            onInputClick = {},
            onSendClick = onCommentSendClick,
        )
    }) {
        ModalBottomSheetLayout(sheetState = sheetState, sheetContent = {
            CommentsScreen(
                uiState = uiState,
                onCommentInputClick = {
                    onEmojiClicked(it)
                    onCommentInputClick()
                },
                onCloseClicked = onCommentsCloseClicked,
                onReplyClicked = onReplyClicked,
            )
        }) {
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            ) { paddingValues ->
                Box {
                    ScrollDetector(pagerState, uiState.videoFeedUiState.onListEndReaching)

                    VerticalPager(
                        count = uiState.videoFeedUiState.items.size,
                        state = pagerState,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        itemSpacing = 10.dp,
                        key = { uiState.videoFeedUiState.items[it].id },
                    ) { index ->
                        when (val item = uiState.videoFeedUiState.items[index]) {
                            is VideoClipUiState.Data -> {
                                VideoClip(
                                    pagerState = pagerState,
                                    index = index,
                                    item = item,
                                    uiState = uiState,
                                    onMuteClicked = onMuteClicked,
                                    onAuthorClicked = onAuthorClicked,
                                    onLikeClicked = onLikeClicked,
                                    onCommentClicked = onCommentClicked,
                                    onShareClicked = onShareClicked
                                )
                            }
                            is VideoClipUiState.Shimmer -> {}
                        }
                    }

                    MainHeader(
                        state = mainHeaderState,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(paddingValues)
                            .padding(top = 16.dp),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun ScrollDetector(
    pagerState: PagerState,
    onListEndReaching: (() -> Unit)?,
) {
    val scrollInfo = remember(pagerState) {
        derivedStateOf {
            val currentPage = pagerState.currentPage
            ScrollInfo(
                isReachingEnd = currentPage + 1 >= pagerState.pageCount - DETECT_THRESHOLD,
                totalItemsCount = pagerState.pageCount,
            )
        }
    }

    DetectScroll(scrollInfo, onListEndReaching)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun PagerScope.VideoClip(
    pagerState: PagerState,
    index: Int,
    item: VideoClipUiState.Data,
    uiState: VideoFeedViewModel.UiState,
    onMuteClicked: (Boolean) -> Unit,
    onAuthorClicked: (VideoClipModel) -> Unit,
    onLikeClicked: (VideoClipModel) -> Unit,
    onCommentClicked: (VideoClipModel) -> Unit,
    onShareClicked: (VideoClipModel) -> Unit
) {
    val shouldPlay by remember(pagerState) {
        derivedStateOf {
            (abs(currentPageOffset) < .5 && currentPage == index) || (abs(currentPageOffset) > .5 && pagerState.currentPage == index)
        }
    }

    ReelPlayer(
        videoClipUrl = item.clip.url,
        shouldPlay = shouldPlay,
        isMuted = uiState.isMuted,
        isScrolling = pagerState.isScrollInProgress,
        onMuted = onMuteClicked,
    )

    VideoClipItem(
        videoClipModel = item.clip,
        onAuthorClicked = onAuthorClicked,
        onLikeClicked = onLikeClicked,
        onCommentClicked = onCommentClicked,
        onShareClicked = onShareClicked,
    )
}

@Composable
private fun VideoClipItem(
    modifier: Modifier = Modifier,
    videoClipModel: VideoClipModel,
    onAuthorClicked: (VideoClipModel) -> Unit,
    onLikeClicked: (VideoClipModel) -> Unit,
    onCommentClicked: (VideoClipModel) -> Unit,
    onShareClicked: (VideoClipModel) -> Unit,
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
                .fillMaxHeight(0.5f)
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        ) {
            VideoClipInfoItems(
                clipModel = videoClipModel,
                onAuthorClicked = onAuthorClicked,
                onLikeClicked = onLikeClicked,
                onCommentClicked = onCommentClicked,
                onShareClicked = onShareClicked,
            )
        }
    }
}

@Composable
private fun VideoClipInfoItems(
    clipModel: VideoClipModel,
    onAuthorClicked: (VideoClipModel) -> Unit,
    onLikeClicked: (VideoClipModel) -> Unit,
    onCommentClicked: (VideoClipModel) -> Unit,
    onShareClicked: (VideoClipModel) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.Bottom,
    ) {
        VideoClipBottomItems(
            modifier = Modifier.fillMaxWidth(0.5f),
            clipModel = clipModel,
        )
        Spacer(modifier = Modifier.weight(1f))
        VideoClipEndItems(
            clipModel = clipModel,
            onAuthorClicked = onAuthorClicked,
            onLikeClicked = onLikeClicked,
            onCommentClicked = onCommentClicked,
            onShareClicked = onShareClicked,
        )
    }
}

@Composable
private fun VideoClipBottomItems(
    modifier: Modifier = Modifier,
    clipModel: VideoClipModel,
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
            Text(
                text = clipModel.title,
                style = AppTheme.specificTypography.bodyLarge,
                color = Color.White,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(4.dp))
            val scrollState = rememberScrollState()
            val interactionSource = remember { MutableInteractionSource() }
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Text(text = clipModel.description,
                    style = AppTheme.specificTypography.bodySmall,
                    maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 2,
                    color = AppTheme.specificColorScheme.white,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                        ) { isDescriptionExpanded = !isDescriptionExpanded }
                        .animateContentSize())
            }
        }
    }
}

@Composable
private fun VideoClipEndItems(
    clipModel: VideoClipModel,
    onAuthorClicked: (VideoClipModel) -> Unit,
    onLikeClicked: (VideoClipModel) -> Unit,
    onCommentClicked: (VideoClipModel) -> Unit,
    onShareClicked: (VideoClipModel) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 16.dp),
    ) {
        Box(
            modifier = Modifier.defaultTileRipple(
                shape = CircleShape,
                onClick = { onAuthorClicked(clipModel) },
            )
        ) {
            Card(
                shape = CircleShape,
                modifier = Modifier.padding(10.dp),
            ) {
                Image(
                    painter = ImageValue.Url("https://picsum.photos/60").get(),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    contentScale = ContentScale.Crop,
                )
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
            icon = if (!clipModel.isLiked) {
                AppTheme.specificIcons.favoriteBorder
            } else {
                AppTheme.specificIcons.favorite
            },
            text = clipModel.likeCount.toString(),
            onIconClicked = { onLikeClicked(clipModel) },
        )

        TextedIcon(
            icon = AppTheme.specificIcons.comment,
            text = clipModel.commentCount.toString(),
            onIconClicked = { onCommentClicked(clipModel) },
        )

        IconButton(onClick = { onShareClicked(clipModel) }) {
            Icon(
                painter = AppTheme.specificIcons.share.get(),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp),
            )
        }
    }
}

@Composable
private fun TextedIcon(
    modifier: Modifier = Modifier,
    icon: IconValue,
    text: String,
    tint: Color = Color.White,
    contentDescription: String? = null,
    onIconClicked: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconButton(onClick = onIconClicked) {
            Icon(
                painter = icon.get(),
                contentDescription = contentDescription,
                tint = tint,
                modifier = modifier.size(36.dp),
            )
        }
        Text(
            text = text,
            color = Color.White,
        )
    }
}