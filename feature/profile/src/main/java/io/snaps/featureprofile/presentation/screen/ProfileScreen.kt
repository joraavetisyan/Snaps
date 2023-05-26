package io.snaps.featureprofile.presentation.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.basefeed.ui.VideoFeedGrid
import io.snaps.corecommon.R
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.startShareLinkIntent
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.doOnClick
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.button.SimpleChip
import io.snaps.coreuicompose.uikit.duplicate.ActionIconData
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
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
        onDismissRequest = viewModel::onDismissRequest,
        onUnsubscribeClicked = viewModel::onUnsubscribeClicked,
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
    onUnsubscribeClicked: (Uuid) -> Unit,
    onDismissRequest: () -> Unit,
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
            SimpleTopAppBar(
                title = when (uiState.userType) {
                    ProfileViewModel.UserType.Other -> "@${uiState.name}"
                    ProfileViewModel.UserType.Current -> LocalStringHolder.current(StringKey.ProfileTitle)
                    ProfileViewModel.UserType.None -> ""
                }.textValue(),
                navigationIcon = AppTheme.specificIcons.back to onBackClicked,
                scrollBehavior = scrollBehavior,
                actions = listOfNotNull(
                    ActionIconData(
                        icon = AppTheme.specificIcons.settings,
                        color = AppTheme.specificColorScheme.darkGrey,
                        onClick = onSettingsClicked,
                    ).takeIf { uiState.userType == ProfileViewModel.UserType.Current },
                    ActionIconData(
                        icon = AppTheme.specificIcons.share,
                        color = AppTheme.specificColorScheme.darkGrey,
                        onClick = { context.startShareLinkIntent(uiState.shareLink!!) },
                    ).takeIf { uiState.shareLink != null },
                ),
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
            uiState.userInfoTileState.Content(modifier = Modifier)
            if (uiState.userType == ProfileViewModel.UserType.Other) {
                Spacer(modifier = Modifier.height(12.dp))
                SimpleChip(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    selected = !uiState.isSubscribed,
                    label = (if (uiState.isSubscribed) StringKey.SubsActionFollowing else StringKey.SubsActionFollow).textValue(),
                    textStyle = AppTheme.specificTypography.titleSmall,
                    contentPadding = PaddingValues(10.dp),
                    onClick = onSubscribeClicked,
                )
            }
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
    when (uiState.dialog) {
        is ProfileViewModel.Dialog.ConfirmUnsubscribe -> ConfirmUnsubscribeDialog(
            data = uiState.dialog.data,
            onDismissRequest = onDismissRequest,
            onUnsubscribeClicked = onUnsubscribeClicked,
        )
        null -> Unit
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
            Icon(
                painter = AppTheme.specificIcons.gallery.get(),
                contentDescription = null,
                tint = colors { if (selectedItemIndex == 0) uiAccent else darkGrey },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(28.dp),
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
            Icon(
                painter = AppTheme.specificIcons.like.get(),
                contentDescription = null,
                tint = colors { if (selectedItemIndex == 1) uiAccent else darkGrey },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(28.dp),
            )
        }
    }
    Divider()
}