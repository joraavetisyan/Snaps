@file:OptIn(ExperimentalFoundationApi::class)

package io.snaps.featuretasks.presentation.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.duplicate.ActionIconData
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featuretasks.ScreenNavigator
import io.snaps.featuretasks.presentation.viewmodel.TaskViewModel

@Composable
fun ShareTaskScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<TaskViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    ShareTaskScreen(
        uiState = uiState,
        onBackClicked = router::back,
        onSaveButtonClicked = viewModel::onSaveButtonClicked,
        onShareIconClicked = viewModel::onShareIconClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShareTaskScreen(
    uiState: TaskViewModel.UiState,
    onBackClicked: () -> Boolean,
    onShareIconClicked: () -> Unit,
    onSaveButtonClicked: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SimpleTopAppBar(
                title = StringKey.TaskShareTitle.textValue(),
                navigationIcon = AppTheme.specificIcons.back to onBackClicked,
                scrollBehavior = scrollBehavior,
                titleHorizontalArrangement = Arrangement.Center,
                actions = listOf(
                    ActionIconData(
                        icon = AppTheme.specificIcons.share,
                        color = AppTheme.specificColorScheme.grey,
                        onClick = onShareIconClicked,
                    ),
                ),
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
            Image(
                painter = ImageValue.ResImage(R.drawable.img_template).get(),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop,
            )
            SimpleButtonActionM(
                onClick = onSaveButtonClicked,
                modifier = Modifier.fillMaxWidth(),
            ) {
                SimpleButtonContent(text = StringKey.ActionSave.textValue())
            }
        }
    }
}