@file:OptIn(ExperimentalFoundationApi::class)

package io.snaps.featuremain.presentation.screen.tasks

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionL
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featuremain.presentation.ScreenNavigator
import io.snaps.featuremain.presentation.screen.SimpleCard
import io.snaps.featuremain.presentation.viewmodel.TaskViewModel

@Composable
fun LikeAndSubscribeTaskScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<TaskViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    LikeAndSubscribeTaskScreen(
        uiState = uiState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LikeAndSubscribeTaskScreen(
    uiState: TaskViewModel.UiState,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SimpleTopAppBar(
                title = StringKey.TaskLikeAndSubscribeTitle.textValue(),
                titleTextStyle = AppTheme.specificTypography.titleLarge,
                navigationIcon = AppTheme.specificIcons.back to { false },
                scrollBehavior = scrollBehavior,
                titleHorizontalArrangement = Arrangement.Center,
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
            SimpleCard {
                TaskProgress(55)
                TaskProgress(55)
                Text(
                    "Like 10 videos and subscribe 5 users",
                    modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 16.dp),
                )
            }
            SimpleButtonActionL(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
                SimpleButtonContent(text = StringKey.ActionStart.textValue())
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
    LikeAndSubscribeTaskScreen(uiState = TaskViewModel.UiState())
}