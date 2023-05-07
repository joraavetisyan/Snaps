package io.snaps.featureprofile.presentation.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.transform.CircleCropTransformation
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuicompose.uikit.listtile.CellTile
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.LeftPart
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
import io.snaps.coreuicompose.uikit.other.TitleSlider
import io.snaps.coreuicompose.uikit.scroll.ScrollEndDetectLazyColumn
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featureprofile.ScreenNavigator
import io.snaps.basesubs.domain.SubModel
import io.snaps.featureprofile.presentation.viewmodel.SubsViewModel
import kotlinx.coroutines.launch

@Composable
fun SubsScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<SubsViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    viewModel.command.collectAsCommand {
        when (it) {
            is SubsViewModel.Command.OpenProfileScreen -> router.toProfileScreen(it.userId)
        }
    }

    SubsScreen(
        uiState = uiState,
        onBackClicked = router::back,
        onDismissRequest = viewModel::onDismissRequest,
        onUnsubscribeClicked = viewModel::onUnsubscribeClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun SubsScreen(
    uiState: SubsViewModel.UiState,
    onBackClicked: () -> Boolean,
    onUnsubscribeClicked: (io.snaps.basesubs.domain.SubModel) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SimpleTopAppBar(
                title = "@${uiState.nickname}".textValue(),
                navigationIcon = AppTheme.specificIcons.back to onBackClicked,
                scrollBehavior = scrollBehavior,
                titleHorizontalArrangement = Arrangement.Center,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .inset(insetAllExcludeTop()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val subscriptions = StringKey.SubsActionSubscriptions.textValue(
                uiState.totalSubscribers
            )
            val subscribers = StringKey.SubsActionSubscribers.textValue(uiState.totalSubscriptions)

            val pages = listOf(uiState.subscriptionsUiState, uiState.subscribersUiState)
            val pagerState = rememberPagerState(uiState.initialPage)

            val coroutineScope = rememberCoroutineScope()

            TitleSlider(
                items = listOf(subscriptions, subscribers),
                selectedItemIndex = pagerState.currentPage,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(it)
                    }
                },
            )
            HorizontalPager(
                pageCount = pages.size,
                state = pagerState,
            ) {
                ScrollEndDetectLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    onScrollEndDetected = pages[pagerState.currentPage].onListEndReaching
                ) {
                    items(pages[it].items, key = { it.userId }) {
                        when (it) {
                            is SubUiState.Data -> Item(it)
                            is SubUiState.Shimmer -> CellTile(
                                data = CellTileState.Data(
                                    leftPart = LeftPart.Shimmer,
                                    middlePart = MiddlePart.Shimmer(
                                        needValueLine = true,
                                    ),
                                    rightPart = RightPart.Shimmer(needCircle = true),
                                )
                            )
                            is SubUiState.Progress -> Box(modifier = Modifier.fillMaxWidth()) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.Center),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    when (uiState.dialog) {
        is SubsViewModel.Dialog.ConfirmUnsubscribe -> ConfirmUnsubscribeDialog(
            data = uiState.dialog.data,
            onDismissRequest = onDismissRequest,
            onUnsubscribeClicked = onUnsubscribeClicked,
        )
        null -> Unit
    }
}

@Composable
private fun Item(
    data: SubUiState.Data,
) {
    val item = data.item
    CellTileState.Data(
        leftPart = item.image?.let {
            LeftPart.Logo(it) { transformations(CircleCropTransformation()) }
        } ?: LeftPart.Shimmer,
        middlePart = MiddlePart.Data(valueBold = item.name.textValue()),
        rightPart = RightPart.ChipData(
            text = (if (item.isSubscribed) StringKey.SubsActionFollowing else StringKey.SubsActionFollow).textValue(),
            selected = !item.isSubscribed,
            onClick = data.onSubscribeClicked,
        ),
        clickListener = data.onClicked,
    ).Content(modifier = Modifier)
}