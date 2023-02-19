@file:OptIn(ExperimentalFoundationApi::class)

package io.snaps.featurecollection.presentation.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.ui.MainHeader
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.uikit.other.TitleSlider
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
import io.snaps.featurecollection.ScreenNavigator
import io.snaps.featurecollection.presentation.viewmodel.MyCollectionViewModel
import kotlinx.coroutines.launch

@Composable
fun MyCollectionScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<MyCollectionViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    val headerState by viewModel.headerUiState.collectAsState()

    viewModel.command.collectAsCommand {
        when (it) {
            is MyCollectionViewModel.Command.OpenRankSelectionScreen -> router.toRankSelectionScreen()
        }
    }

    MyCollectionScreen(
        uiState = uiState,
        headerState = headerState,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
private fun MyCollectionScreen(
    uiState: MyCollectionViewModel.UiState,
    headerState: MainHeaderHandler.UiState,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val current = StringKey.MyCollectionTitleSlideNft.textValue()
    val history = StringKey.MyCollectionTitleSlideMysteryBox.textValue()

    val pages = listOf(uiState.nft, uiState.mysteryBox)
    val pagerState = rememberPagerState()

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {},
    ) {
        Column(
            modifier = Modifier
                .background(color = AppTheme.specificColorScheme.uiContentBg)
                .padding(it),
        ) {
            MainHeader(state = headerState.value)
            Header()
            Spacer(modifier = Modifier.height(12.dp))
            TitleSlider(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                items = listOf(current, history),
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
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (pagerState.currentPage == 0) {
                        items(uiState.nft) {
                            it.Content(modifier = Modifier)
                        }
                    } else {
                        items(uiState.mysteryBox) {
                            it.Content(modifier = Modifier)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Header() {
    Column(
        Modifier.padding(12.dp)
    ) {
        Text(text = LocalStringHolder.current(StringKey.MyCollectionTitle), style = AppTheme.specificTypography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = LocalStringHolder.current(StringKey.MyCollectionMessage),
            style = AppTheme.specificTypography.titleSmall,
            color = AppTheme.specificColorScheme.textSecondary,
        )
    }
}