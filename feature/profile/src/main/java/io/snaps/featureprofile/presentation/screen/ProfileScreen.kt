package io.snaps.featureprofile.presentation.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.basefeed.ui.VideoFeedGrid
import io.snaps.corecommon.R
import io.snaps.corecommon.container.IconValue
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.startShareLinkIntent
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.doOnClick
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeBottom
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.tools.insetTop
import io.snaps.coreuicompose.tools.toPx
import io.snaps.coreuicompose.uikit.button.SimpleChip
import io.snaps.coreuicompose.uikit.duplicate.ActionIconData
import io.snaps.coreuicompose.uikit.duplicate.OnBackIconClick
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBarConfig
import io.snaps.coreuicompose.uikit.duplicate.TopAppBarActionIcon
import io.snaps.coreuicompose.uikit.duplicate.TopAppBarLayout
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.colors
import io.snaps.featureprofile.ScreenNavigator
import io.snaps.featureprofile.presentation.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<ProfileViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    viewModel.command.collectAsCommand {
        when (it) {
            ProfileViewModel.Command.OpenSettingsScreen -> router.toSettingsScreen()
            is ProfileViewModel.Command.OpenSubsScreen -> router.toSubsScreen(it.args)
            is ProfileViewModel.Command.OpenUserFeedScreen -> router.toUserFeedScreen(
                userId = it.userId, position = it.position
            )
            is ProfileViewModel.Command.OpenLikedFeedScreen -> router.toLikedFeedScreen(
                userId = it.userId, position = it.position,
            )
        }
    }

    ProfileScreen(
        uiState = uiState,
        onCreateVideoClicked = router::toCreateVideoScreen,
        onSettingsClicked = viewModel::onSettingsClicked,
        onBackClicked = router::back,
        onSubscribeClicked = viewModel::onSubscribeClicked,
        onVideoClipClicked = viewModel::onVideoClipClicked,
        onUserLikedVideoClipClicked = viewModel::onUserLikedVideoClipClicked,
        onLikeIconClicked = viewModel::onLikeIconClicked,
        onGalleryIconClicked = viewModel::onGalleryIconClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileScreen(
    uiState: ProfileViewModel.UiState,
    onCreateVideoClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onBackClicked: () -> Boolean,
    onSubscribeClicked: () -> Unit,
    onVideoClipClicked: (Int) -> Unit,
    onUserLikedVideoClipClicked: (Int) -> Unit,
    onLikeIconClicked: () -> Unit,
    onGalleryIconClicked: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                title = when (uiState.userType) {
                    ProfileViewModel.UserType.Other,
                    ProfileViewModel.UserType.Current -> "@${uiState.name}"
                    ProfileViewModel.UserType.None -> ""
                },
                navigationIcon = AppTheme.specificIcons.back to onBackClicked,
                actions = listOfNotNull(
                    ActionIconData(
                        icon = AppTheme.specificIcons.settings,
                        color = AppTheme.specificColorScheme.white,
                        onClick = onSettingsClicked,
                    ).takeIf { uiState.userType == ProfileViewModel.UserType.Current },
                    ActionIconData(
                        icon = AppTheme.specificIcons.share,
                        color = AppTheme.specificColorScheme.white,
                        onClick = { context.startShareLinkIntent(uiState.shareLink!!) },
                    ).takeIf { uiState.shareLink != null },
                ),
                userInfoTileState = uiState.userInfoTileState,
                userType = uiState.userType,
                isSubscribed = uiState.isSubscribed,
                onSubscribeClicked = onSubscribeClicked,
            )
        },
        floatingActionButton = {
            if (uiState.userType == ProfileViewModel.UserType.Current) {
                IconButton(onClick = { onCreateVideoClicked() }) {
                    Icon(
                        painter = R.drawable.img_create.imageValue().get(),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(64.dp),
                    )
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .inset(insetAllExcludeTop()),
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Actions(
                selectedItemIndex = uiState.selectedItemIndex,
                onGalleryIconClicked = onGalleryIconClicked,
                onLikeIconClicked = onLikeIconClicked,
            )
            Spacer(modifier = Modifier.height(12.dp))
            AnimatedContent(
                targetState = uiState.selectedItemIndex,
                modifier = Modifier.fillMaxSize(),
                label = "",
            ) {
                when (it) {
                    0 -> VideoFeedGrid(
                        columnCount = 3,
                        uiState = uiState.videoFeedUiState,
                        onClick = onVideoClipClicked,
                    )
                    1 -> VideoFeedGrid(
                        columnCount = 3,
                        uiState = uiState.userLikedVideoFeedUiState,
                        onClick = onUserLikedVideoClipClicked,
                    )
                }
            }
        }
    }
}

@Composable
private fun Actions(
    selectedItemIndex: Int,
    onGalleryIconClicked: () -> Unit,
    onLikeIconClicked: () -> Unit,
) {
    Divider()
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .doOnClick(onClick = onGalleryIconClicked),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Shorts",
                style = AppTheme.specificTypography.titleSmall,
                color = colors { if (selectedItemIndex == 0) textPrimary else darkGrey },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .doOnClick(onClick = onLikeIconClicked),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Liked",
                style = AppTheme.specificTypography.titleSmall,
                color = colors { if (selectedItemIndex == 1) textPrimary else darkGrey },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
    Divider()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    title: String,
    navigationIcon: Pair<IconValue, OnBackIconClick>,
    actions: List<ActionIconData>,
    userInfoTileState: UserInfoTileState,
    userType: ProfileViewModel.UserType,
    isSubscribed: Boolean,
    onSubscribeClicked: () -> Unit,
) {
    val colors = SimpleTopAppBarConfig.transparentSurfaceColors()
    val imageHeight = 64.dp + insetTop().asPaddingValues().calculateTopPadding() + 16.dp + 76.dp

    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Image(
            painter = R.drawable.img_profile_background.imageValue().get(),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight),
        )
        Column {
            TopAppBarLayout(
                modifier = Modifier
                    .windowInsetsPadding(insetAllExcludeBottom())
                    .clipToBounds(),
                heightPx = 64.dp.toPx(),
                navigationIconContentColor = colors.navigationIconContentColor,
                titleContentColor = colors.titleContentColor,
                actionIconContentColor = colors.actionIconContentColor,
                title = {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 16.dp),
                        color = AppTheme.specificColorScheme.white,
                    )
                },
                titleTextStyle = AppTheme.specificTypography.titleMedium,
                titleAlpha = 1f,
                titleVerticalArrangement = Arrangement.Center,
                titleHorizontalArrangement = Arrangement.Start,
                titleBottomPadding = 0,
                hideTitleSemantics = false,
                navigationIcon = {
                    Icon(
                        painter = navigationIcon.first.get(),
                        tint = AppTheme.specificColorScheme.white,
                        contentDescription = "navigation icon",
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable(onClick = { navigationIcon.second() })
                            .padding(8.dp),
                    )
                },
                actions = {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        actions.toList().forEach { TopAppBarActionIcon(data = it) }
                    }
                },
            )
            userInfoTileState.Content(modifier = Modifier)
            if (userType == ProfileViewModel.UserType.Other) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = AppTheme.specificColorScheme.white,
                            shape = RoundedCornerShape(
                                topEnd = 0.dp,
                                topStart = 0.dp,
                                bottomStart = 12.dp,
                                bottomEnd = 12.dp,
                            )
                        )
                        .padding(bottom = 16.dp),
                ) {
                    SimpleChip(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        selected = !isSubscribed,
                        label = (if (isSubscribed) StringKey.SubsActionFollowing else StringKey.SubsActionFollow).textValue(),
                        textStyle = AppTheme.specificTypography.titleSmall,
                        contentPadding = PaddingValues(10.dp),
                        onClick = onSubscribeClicked,
                    )
                }
            }
        }
    }
}