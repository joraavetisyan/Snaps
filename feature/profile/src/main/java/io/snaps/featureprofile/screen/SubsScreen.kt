@file:OptIn(ExperimentalFoundationApi::class)

package io.snaps.featureprofile.screen

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.transform.CircleCropTransformation
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.LeftPart
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
import io.snaps.coreuicompose.uikit.other.TitleSlider
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featureprofile.ScreenNavigator
import io.snaps.featureprofile.domain.Sub
import io.snaps.featureprofile.viewmodel.SubsViewModel
import kotlinx.coroutines.launch

@Composable
fun SubsScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<SubsViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    SubsScreen(
        uiState = uiState,
        onBackClicked = router::back,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
private fun SubsScreen(
    uiState: SubsViewModel.UiState,
    onBackClicked: () -> Boolean,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SimpleTopAppBar(
                title = "@${uiState.nickname}".textValue(),
                titleTextStyle = AppTheme.specificTypography.titleLarge,
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
            val subscriptions = StringKey.SubsActionSubscriptions.textValue(uiState.totalSubscribers)
            val subscribers = StringKey.SubsActionSubscribers.textValue(uiState.totalSubscriptions)

            val pages = listOf(uiState.subscriptions, uiState.subscribers)
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
                count = pages.size,
                state = pagerState,
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(pages[pagerState.currentPage]) { Item(it) }
                }
            }
        }
    }
}

@Composable
private fun Item(item: Sub) {
    CellTileState.Data(
        leftPart = LeftPart.Logo(item.image) { transformations(CircleCropTransformation()) },
        middlePart = MiddlePart.Data(valueBold = item.name.textValue()),
        rightPart = RightPart.ButtonData(
            text = (if (item.isSubscribed) StringKey.SubsActionFollowing else StringKey.SubsActionFollow).textValue(),
            enable = !item.isSubscribed,
            onClick = {},
        )
    ).Content(modifier = Modifier)
}