@file:OptIn(ExperimentalFoundationApi::class)

package io.snaps.featuretasks.presentation.screen

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import io.snaps.basenft.ui.CollectionItemState
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.ui.MainHeaderState
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.other.SimpleCard
import io.snaps.coreuicompose.uikit.other.TitleSlider
import io.snaps.coreuicompose.uikit.scroll.ScrollEndDetectLazyColumn
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featuretasks.ScreenNavigator
import io.snaps.featuretasks.presentation.historyTasksItems
import io.snaps.featuretasks.presentation.ui.RemainingTimeTileState
import io.snaps.featuretasks.presentation.viewmodel.TasksViewModel
import kotlinx.coroutines.launch

@Composable
fun TasksScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<TasksViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    val mainHeaderState by viewModel.headerUiState.collectAsState()

    viewModel.command.collectAsCommand {
        when (it) {
            is TasksViewModel.Command.OpenTaskDetailsScreen -> router.toTaskDetailsScreen(it.args)
        }
    }

    TasksScreen(
        uiState = uiState,
        mainHeaderState = mainHeaderState,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
private fun TasksScreen(
    uiState: TasksViewModel.UiState,
    mainHeaderState: MainHeaderHandler.UiState,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {},
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .inset(insetAllExcludeTop()),
        ) {
            val current = StringKey.TasksTitleSlideCurrent.textValue()
            val history = StringKey.TasksTitleSlideHistory.textValue()

            val pages = listOf(uiState.current, uiState.history)
            val pagerState = rememberPagerState()

            val coroutineScope = rememberCoroutineScope()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Card(
                    shape = CircleShape,
                ) {
                    (mainHeaderState.value as? MainHeaderState.Data)?.profileImage?.let {
                        Image(
                            painter = it.get(),
                            contentDescription = null,
                            modifier = Modifier.size(44.dp),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
                TitleSlider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    items = listOf(current, history),
                    selectedItemIndex = pagerState.currentPage,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(it)
                        }
                    },
                )
            }
            if (pagerState.currentPage == 0) {
                Titles(
                    StringKey.TasksTitleCurrent.textValue(),
                    StringKey.TasksTitleMessageCurrent.textValue(),
                )
            } else {
                Titles(
                    StringKey.TasksTitleHistory.textValue(),
                    StringKey.TasksTitleMessageHistory.textValue(),
                )
            }
            HorizontalPager(
                count = pages.size,
                state = pagerState,
            ) {
                if (pagerState.currentPage == 0) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = 12.dp,
                            start = 12.dp,
                            end = 12.dp,
                            bottom = 60.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        item {
                           NftBlock(
                               userNtfCollection = uiState.userNftCollection,
                               remainingTime = uiState.remainingTime,
                           )
                        }
                        items(uiState.current) {
                            it.Content(modifier = Modifier)
                        }
                    }
                } else {
                    ScrollEndDetectLazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = 12.dp,
                            start = 12.dp,
                            end = 12.dp,
                            bottom = 60.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        onScrollEndDetected = uiState.history.onListEndReaching,
                    ) {
                        historyTasksItems(
                            uiState = uiState.history,
                            modifier = Modifier,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Titles(title1: TextValue, title2: TextValue) {
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title1.get(),
            style = AppTheme.specificTypography.titleLarge,
        )
        Text(
            text = title2.get(),
            style = AppTheme.specificTypography.titleSmall,
            color = AppTheme.specificColorScheme.textSecondary,
        )
    }
}

@Composable
private fun NftBlock(
    userNtfCollection: List<CollectionItemState>,
    remainingTime: RemainingTimeTileState,
) {
    SimpleCard(
        modifier = Modifier.fillMaxWidth(),
        color = AppTheme.specificColorScheme.white,
    ) {
        remainingTime.Content(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(top = 12.dp),
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(12.dp),
        ) {
            items(userNtfCollection) {
                it.Content(modifier = Modifier.width(180.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
    TasksScreen(uiState = TasksViewModel.UiState(), mainHeaderState = MainHeaderHandler.UiState())
}