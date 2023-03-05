@file:OptIn(ExperimentalFoundationApi::class)

package io.snaps.featuretasks.presentation.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
import io.snaps.coreuicompose.uikit.other.ShimmerTile
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featuretasks.ScreenNavigator
import io.snaps.coreuicompose.uikit.other.SimpleCard
import io.snaps.featuretasks.presentation.ui.TaskProgress
import io.snaps.featuretasks.presentation.ui.TaskToolbar
import io.snaps.featuretasks.presentation.viewmodel.TaskViewModel

@Composable
fun LikeAndSubscribeTaskScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<TaskViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    LikeAndSubscribeTaskScreen(
        uiState = uiState,
        onBackClicked = router::back,
        onStartButtonClicked = viewModel::onStartButtonClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LikeAndSubscribeTaskScreen(
    uiState: TaskViewModel.UiState,
    onBackClicked: () -> Boolean,
    onStartButtonClicked: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TaskToolbar(
                title = StringKey.TaskWatchVideoTitle.textValue(),
                navigationIcon = AppTheme.specificIcons.back to onBackClicked,
                progress = uiState.energy,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .inset(insetAllExcludeTop())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Content(
                uiState = uiState,
                onStartButtonClicked = onStartButtonClicked,
            )
            uiState.messageBannerState?.Content(modifier = Modifier)
            if (uiState.isLoading) {
                Shimmer()
            }
        }
    }
}

@Composable
private fun Content(
    uiState: TaskViewModel.UiState,
    onStartButtonClicked: () -> Unit,
) {
    SimpleCard {
        TaskProgress(
            progress = uiState.energyProgress,
            maxValue = uiState.energy,
        )
        Text(
            text = uiState.description,
            style = AppTheme.specificTypography.bodySmall,
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 16.dp),
        )
    }
    SimpleButtonActionM(
        onClick = onStartButtonClicked,
        modifier = Modifier.fillMaxWidth(),
    ) {
        SimpleButtonContent(text = StringKey.ActionStart.textValue())
    }
}

@Composable
private fun Shimmer() {
    SimpleCard {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row {
                MiddlePart.Shimmer(needValueLine = true).Content(modifier = Modifier)
                RightPart.Shimmer(needRightLine = true).Content(modifier = Modifier)
            }
            Row {
                MiddlePart.Shimmer(needValueLine = true).Content(modifier = Modifier)
                RightPart.Shimmer(needRightLine = true).Content(modifier = Modifier)
            }
            MiddlePart.Shimmer(needValueLine = true).Content(modifier = Modifier)
        }
    }
    ShimmerTile(
        shape = AppTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
    )
}