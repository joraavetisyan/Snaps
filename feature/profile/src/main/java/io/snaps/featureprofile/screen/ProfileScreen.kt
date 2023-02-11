package io.snaps.featureprofile.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.basefeed.ui.VideoFeedGrid
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.button.SimpleChip
import io.snaps.coreuicompose.uikit.duplicate.ActionIconData
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
import io.snaps.featureprofile.ScreenNavigator
import io.snaps.featureprofile.viewmodel.ProfileViewModel

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
        }
    }

    ProfileScreen(
        uiState = uiState,
        onSettingsClicked = viewModel::onSettingsClicked,
        onBackClicked = router::back,
        onSubscribeClicked = viewModel::onSubscribeClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
private fun ProfileScreen(
    uiState: ProfileViewModel.UiState,
    onSettingsClicked: () -> Unit,
    onBackClicked: () -> Boolean,
    onSubscribeClicked: () -> Unit,
) {
    val title = when (uiState.userType) {
        ProfileViewModel.UserType.Other -> "@${uiState.nickname}"
        ProfileViewModel.UserType.Current -> LocalStringHolder.current(StringKey.ProfileTitle)
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SimpleTopAppBar(
                title = {
                    Text(text = title)
                },
                navigationIcon = (AppTheme.specificIcons.back to onBackClicked)
                    .takeIf { uiState.userType == ProfileViewModel.UserType.Other },
                titleTextStyle = AppTheme.specificTypography.titleLarge,
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
                        onClick = {},
                    ),
                ),
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
        ) {
            uiState.userInfoTileState.Content(modifier = Modifier)
            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Icon(
                    painter = AppTheme.specificIcons.gallery.get(),
                    contentDescription = null,
                    tint = AppTheme.specificColorScheme.darkGrey,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(28.dp),
                )
                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
                Icon(
                    painter = AppTheme.specificIcons.like.get(),
                    contentDescription = null,
                    tint = AppTheme.specificColorScheme.darkGrey,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(28.dp),
                )
            }
            Divider()
            Spacer(modifier = Modifier.height(12.dp))
            if (uiState.userType == ProfileViewModel.UserType.Other) {
                SimpleChip(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(elevation = 12.dp, shape = CircleShape),
                    selected = !uiState.isSubscribed,
                    label = (if (uiState.isSubscribed) StringKey.SubsActionFollowing else StringKey.SubsActionFollow).textValue(),
                    onClick = onSubscribeClicked,
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            VideoFeedGrid(columnCount = 3, uiState = uiState.videoFeedUiState)
        }
    }
}