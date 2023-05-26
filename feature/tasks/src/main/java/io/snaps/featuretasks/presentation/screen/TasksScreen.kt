@file:OptIn(ExperimentalFoundationApi::class)

package io.snaps.featuretasks.presentation.screen

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.basenft.ui.CollectionItemState
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.ui.MainHeader
import io.snaps.baseprofile.ui.MainHeaderState
import io.snaps.corecommon.R
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.bottomsheetdialog.FootnoteBottomDialog
import io.snaps.coreuicompose.uikit.bottomsheetdialog.FootnoteBottomDialogItem
import io.snaps.coreuicompose.uikit.bottomsheetdialog.ModalBottomSheetTargetStateListener
import io.snaps.coreuicompose.uikit.other.SimpleCard
import io.snaps.coreuicompose.uikit.other.TitleSlider
import io.snaps.coreuicompose.uikit.scroll.ScrollEndDetectLazyColumn
import io.snaps.coreuicompose.uikit.status.FootnoteUi
import io.snaps.coreuicompose.uikit.status.InfoBlock
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featuretasks.ScreenNavigator
import io.snaps.featuretasks.presentation.historyTasksItems
import io.snaps.featuretasks.presentation.ui.RemainingTimeTileState
import io.snaps.featuretasks.presentation.ui.TaskProgress
import io.snaps.featuretasks.presentation.viewmodel.TasksViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun TasksScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<TasksViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    val mainHeaderState by viewModel.headerUiState.collectAsState()
    val pagerState = rememberPagerState()
    val pullRefreshState = rememberPullRefreshState(uiState.isRefreshing, { viewModel.onRefreshPulled(pagerState.currentPage) })

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )

    viewModel.command.collectAsCommand {
        when (it) {
            TasksViewModel.Command.ShowBottomDialog -> coroutineScope.launch { sheetState.show() }
            TasksViewModel.Command.HideBottomDialog -> coroutineScope.launch { sheetState.hide() }
            is TasksViewModel.Command.OpenTaskDetailsScreen -> router.toTaskDetailsScreen(it.args)
            is TasksViewModel.Command.OpenNftDetailsScreen -> router.toUserNftDetailsScreen(it.args)
        }
    }

    viewModel.headerCommand.collectAsCommand {
        when (it) {
            MainHeaderHandler.Command.OpenProfileScreen -> router.toProfileScreen()
            MainHeaderHandler.Command.OpenWalletScreen -> router.toWalletScreen()
        }
    }

    ModalBottomSheetTargetStateListener(
        sheetState = sheetState,
        onStateToChange = viewModel::onBottomDialogStateChange,
    )

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            when (uiState.bottomDialog) {
                TasksViewModel.BottomDialog.CurrentTasksFootnote,
                TasksViewModel.BottomDialog.HistoryTasksFootnote -> FootnoteBottomDialog(
                    FootnoteBottomDialogItem(
                        image = R.drawable.img_guy_eating.imageValue(),
                        title = StringKey.TasksDialogTitleFootnote1.textValue(),
                        text = StringKey.TasksDialogMessageFootnote1.textValue(),
                    ),
                    FootnoteBottomDialogItem(
                        image = R.drawable.img_guy_glad.imageValue(),
                        title = StringKey.TasksDialogTitleFootnote2.textValue(),
                        text = StringKey.TasksDialogMessageFootnote2.textValue(),
                    ),
                    FootnoteBottomDialogItem(
                        image = R.drawable.img_guy_surprised.imageValue(),
                        title = StringKey.TasksDialogTitleFootnote3.textValue(),
                        text = StringKey.TasksDialogMessageFootnote3.textValue(),
                        onClick = viewModel::onFootnoteStartClicked,
                        buttonText = StringKey.ActionStart.textValue(),
                    ),
                )
            }
        }
    ) {
        TasksScreen(
            uiState = uiState,
            headerState = mainHeaderState.value,
            pagerState = pagerState,
            pullRefreshState = pullRefreshState,
            onCurrentTasksFootnoteClick = viewModel::onCurrentTasksFootnoteClick,
            onHistoryTasksFootnoteClick = viewModel::onHistoryTasksFootnoteClick,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
private fun TasksScreen(
    uiState: TasksViewModel.UiState,
    headerState: MainHeaderState,
    pagerState: PagerState,
    pullRefreshState: PullRefreshState,
    onCurrentTasksFootnoteClick: () -> Unit,
    onHistoryTasksFootnoteClick: () -> Unit,
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

            MainHeader(state = headerState)

            val coroutineScope = rememberCoroutineScope()

            val current = StringKey.TasksTitleSlideCurrent.textValue()
            val history = StringKey.TasksTitleSlideHistory.textValue()

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
            Box(
                modifier = Modifier.pullRefresh(pullRefreshState),
            ) {
                HorizontalPager(
                    pageCount = 2,
                    state = pagerState,
                ) { page ->
                    val contentPadding = PaddingValues(
                        top = 12.dp,
                        start = 12.dp,
                        end = 12.dp,
                        bottom = 100.dp,
                    )
                    when (page) {
                        0 -> LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = contentPadding,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            item {
                                FootnoteUi(
                                    title = StringKey.TasksTitleFootnoteCurrent.textValue(),
                                    description = StringKey.TasksMessageFootnoteCurrent.textValue(),
                                    action = StringKey.ActionHowItWorks.textValue(),
                                    onClick = onCurrentTasksFootnoteClick,
                                    padding = 0.dp,
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                NftBlock(
                                    userNtfCollection = uiState.userNftCollection,
                                    remainingTime = uiState.remainingTime,
                                    energy = uiState.totalEnergy,
                                    energyProgress = uiState.totalEnergyProgress,
                                )
                                if (uiState.countBrokenGlasses > 0) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    InfoBlock(
                                        message = StringKey.TasksErrorRepairGlasses.textValue(),
                                        textColor = AppTheme.specificColorScheme.uiSystemRed,
                                        backgroundColor = AppTheme.specificColorScheme.uiSystemRed.copy(alpha = 0.3f),
                                    )
                                }
                            }
                            items(uiState.current) {
                                it.Content(modifier = Modifier)
                            }
                        }

                        1 -> ScrollEndDetectLazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = contentPadding,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            onScrollEndDetected = uiState.history.onListEndReaching,
                        ) {
                            item {
                                FootnoteUi(
                                    title = StringKey.TasksTitleFootnoteHistory.textValue(),
                                    description = StringKey.TasksMessageFootnoteHistory.textValue(),
                                    action = StringKey.ActionHowItWorks.textValue(),
                                    onClick = onHistoryTasksFootnoteClick,
                                    padding = 0.dp,
                                )
                            }
                            historyTasksItems(
                                uiState = uiState.history,
                                modifier = Modifier,
                            )
                        }
                    }
                }
                PullRefreshIndicator(
                    refreshing = uiState.isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }
        }
    }
}

@Composable
private fun NftBlock(
    userNtfCollection: List<CollectionItemState>,
    remainingTime: RemainingTimeTileState,
    energy: Int,
    energyProgress: Int,
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
        if (remainingTime is RemainingTimeTileState.Data) {
            TaskProgress(
                modifier = Modifier.padding(horizontal = 12.dp),
                progress = energyProgress,
                maxValue = energy,
            )
        }
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
}