@file:OptIn(ExperimentalFoundationApi::class)

package io.snaps.featuretasks.presentation.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
import io.snaps.coreuicompose.uikit.other.SimpleCard
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featuretasks.ScreenNavigator
import io.snaps.featuretasks.presentation.ui.TaskToolbar
import io.snaps.featuretasks.presentation.viewmodel.TaskViewModel

@Composable
fun WatchVideoTaskScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<TaskViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    WatchVideoTaskScreen(
        uiState = uiState,
        onBackClicked = router::back,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WatchVideoTaskScreen(
    uiState: TaskViewModel.UiState,
    onBackClicked: () -> Boolean,
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
            Content(uiState = uiState)
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
) {
    SimpleCard {
        TaskProgress(
            progress = uiState.energyProgress,
            maxValue = uiState.energy,
        )
        Text(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 16.dp),
            text = uiState.description,
            style = AppTheme.specificTypography.bodySmall,
        )
    }
    if (uiState.completed) {
        TaskCompletedMessage()
    }
}

@Composable
private fun Shimmer() {
    SimpleCard {
        Row {
            MiddlePart.Shimmer(
                needValueLine = true,
                needDescriptionLine = true,
            ).Content(modifier = Modifier.padding(12.dp))
            RightPart.Shimmer(
                needRightLine = true,
            ).Content(modifier = Modifier.padding(12.dp))
        }
    }
}

@Composable
private fun TaskCompletedMessage() {
    SimpleCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = AppTheme.specificColorScheme.uiSystemGreen.copy(0.2f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                StringKey.TaskWatchVideoFieldJobCompleted.textValue().get(),
                color = AppTheme.specificColorScheme.textGreen,
                style = AppTheme.specificTypography.bodySmall,
            )
            Image(
                painter = ImageValue.ResImage(R.drawable.img_fire).get(),
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                contentScale = ContentScale.Crop,
            )
        }
    }
}