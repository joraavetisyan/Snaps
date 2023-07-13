package io.snaps.featureprofile.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import io.snaps.basenotifications.data.model.NotificationType
import io.snaps.basenotifications.domain.NotificationModel
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuicompose.uikit.listtile.CellTile
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.LeftPart
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
import io.snaps.coreuicompose.uikit.scroll.ScrollEndDetectLazyColumn
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featureprofile.ScreenNavigator
import io.snaps.coreuicompose.tools.toPx
import io.snaps.featureprofile.presentation.NotificationUiState
import io.snaps.featureprofile.presentation.toActionText
import io.snaps.featureprofile.presentation.viewmodel.NotificationsViewModel

@Composable
fun NotificationsScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<NotificationsViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    viewModel.command.collectAsCommand {
        when (it) {
            is NotificationsViewModel.Command.OpenProfileScreen -> router.toProfileScreen(it.userId)
            NotificationsViewModel.Command.OpenMainScreen -> router.toMainVideoFeedScreen()
        }
    }

    NotificationsScreen(
        uiState = uiState,
        onBackClicked = router::back,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationsScreen(
    uiState: NotificationsViewModel.UiState,
    onBackClicked: () -> Boolean,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SimpleTopAppBar(
                title = StringKey.NotificationsTitle.textValue(),
                navigationIcon = AppTheme.specificIcons.back to onBackClicked,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        ScrollEndDetectLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .inset(insetAllExcludeTop()),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            onScrollEndDetected = uiState.notificationsUiState.onListEndReaching,
        ) {
            itemsIndexed(
                items = uiState.notificationsUiState.items,
                key = { _, item -> item.key }
            ) { index, item ->
                Notification(item)
                if (index < uiState.notificationsUiState.count) {
                    Divider(color = AppTheme.specificColorScheme.black_10, thickness = 1.dp)
                }
            }
            item {
                uiState.notificationsUiState.errorState?.Content(modifier = Modifier.fillMaxSize())
                uiState.notificationsUiState.emptyState?.Content(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun Notification(data: NotificationUiState) {
    when (data) {
        is NotificationUiState.Data -> {
            NotificationItem(
                notification = data.item,
                onSubscribeClicked = data.onSubscribeClicked,
                onNotificationClicked = data.onClicked,
            )
        }
        is NotificationUiState.Shimmer -> CellTile(
            data = CellTileState.Data(
                leftPart = LeftPart.Shimmer,
                middlePart = MiddlePart.Shimmer(
                    needValueLine = true,
                ),
                rightPart = RightPart.Shimmer(needCircle = true),
            )
        )
        is NotificationUiState.Progress -> Box(modifier = Modifier.fillMaxWidth()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center),
            )
        }
    }
}

@Composable
private fun NotificationItem(
    notification: NotificationModel,
    onSubscribeClicked: () -> Unit,
    onNotificationClicked: () -> Unit,
) {
    val middleText = buildAnnotatedString {
        val userName = notification.actionCreateUserName
        val actionText = notification.type.toActionText(notification.actionCreateUserName).get()
        append(actionText)
        val startIndex = actionText.indexOf(userName)
        val endIndex = startIndex + userName.length
        addStyle(
            style = SpanStyle(
                color = AppTheme.specificColorScheme.textPrimary,
                fontWeight = FontWeight.Bold,
            ),
            start = startIndex,
            end = endIndex,
        )
    }
    val rightPart = when (notification.type) {
        NotificationType.Follow -> RightPart.ChipData(
            text = when (notification.isSubscribed) {
                true -> StringKey.SubsActionFollowing
                false -> StringKey.SubsActionFollow
            }.textValue(),
            selected = !notification.isSubscribed,
            onClick = onSubscribeClicked,
        )
        NotificationType.Comment,
        NotificationType.Like -> notification.videoImage?.let {
            val videoImageRounded = RoundedCornersTransformation(4.dp.toPx())
            RightPart.Logo(source = it) { transformations(videoImageRounded) }
        }
    }
    CellTileState.Data(
        leftPart = LeftPart.Logo(notification.actionCreateUserAvatar) {
            transformations(CircleCropTransformation())
        },
        middlePart = MiddlePart.Data(
            value = middleText.textValue(),
            action = notification.text.orEmpty().textValue().takeIf {
                notification.type == NotificationType.Comment
            },
        ),
        rightPart = rightPart,
        clickListener = onNotificationClicked,
    ).Content(modifier = Modifier)
}