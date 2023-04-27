package io.snaps.featuresearch.presentation.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.transform.CircleCropTransformation
import io.snaps.basefeed.ui.VideoFeedGrid
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.ui.MainHeader
import io.snaps.baseprofile.ui.MainHeaderState
import io.snaps.baseprofile.ui.UserUiState
import io.snaps.baseprofile.ui.UsersUiState
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.input.SimpleTextField
import io.snaps.coreuicompose.uikit.listtile.CellTile
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.LeftPart
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.other.TitleSlider
import io.snaps.coreuicompose.uikit.scroll.ScrollEndDetectLazyColumn
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featuresearch.ScreenNavigator
import io.snaps.featuresearch.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<SearchViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    val headerState by viewModel.headerUiState.collectAsState()

    viewModel.command.collectAsCommand {
        when (it) {
            is SearchViewModel.Command.OpenPopularVideoFeedScreen -> {
                router.toPopularVideoFeedScreen(query = it.query, position = it.position)
            }
            is SearchViewModel.Command.OpenProfileScreen -> router.toProfileScreen(it.userId)
        }
    }

    viewModel.headerCommand.collectAsCommand {
        when (it) {
            MainHeaderHandler.Command.OpenProfileScreen -> router.toProfileScreen()
            MainHeaderHandler.Command.OpenWalletScreen -> router.toWalletScreen()
        }
    }

    SearchScreen(
        uiState = uiState,
        headerState = headerState.value,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onClick = viewModel::onItemClicked,
        onClearQueryClicked = viewModel::onClearQueryClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun SearchScreen(
    uiState: SearchViewModel.UiState,
    headerState: MainHeaderState,
    onSearchQueryChanged: (String) -> Unit,
    onClick: (Int) -> Unit,
    onClearQueryClicked: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val pages = listOf(uiState.videoFeedUiState, uiState.usersUiState)
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

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
            SimpleTextField(
                value = uiState.query,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                placeholder = {
                    Text(text = StringKey.SearchHint.textValue().get())
                },
                leadingIcon = {
                    Icon(
                        painter = AppTheme.specificIcons.search.get(),
                        contentDescription = null,
                        tint = AppTheme.specificColorScheme.darkGrey,
                    )
                },
                trailingIcon = {
                    Icon(
                        painter = AppTheme.specificIcons.close.get(),
                        contentDescription = null,
                        tint = AppTheme.specificColorScheme.darkGrey,
                        modifier = Modifier.defaultTileRipple(onClick = onClearQueryClicked)
                    )
                },
                maxLines = 1,
            )
            TitleSlider(
                items = listOf(
                    StringKey.SearchVideosTitle.textValue(),
                    StringKey.SearchProfilesTitle.textValue(),
                ),
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
                when (it) {
                    0 -> VideoFeedGrid(
                        columnCount = 2,
                        uiState = uiState.videoFeedUiState,
                        onClick = onClick,
                    )
                    1 -> Users(uiState = uiState.usersUiState)
                }
            }
        }
    }
}

@Composable
private fun Users(
    uiState: UsersUiState,
) {
    ScrollEndDetectLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        onScrollEndDetected = uiState.onListEndReaching,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        itemsIndexed(
            items = uiState.items,
            key = { _, item -> item.key }
        ) { _, it ->
            when (it) {
                is UserUiState.Data -> CellTile(
                    data = CellTileState.Data(
                        leftPart = it.user.avatar.let {
                            LeftPart.Logo(it) { transformations(CircleCropTransformation()) }
                        },
                        middlePart = MiddlePart.Data(valueBold = it.user.name.textValue()),
                        clickListener = it.onClicked,
                    )
                )
                is UserUiState.Shimmer -> CellTile(
                    data = CellTileState.Data(
                        leftPart = LeftPart.Shimmer,
                        middlePart = MiddlePart.Shimmer(needValueLine = true),
                    )
                )
            }
        }
        item {
            uiState.errorState?.Content(modifier = Modifier)
            uiState.emptyState?.Content(modifier = Modifier)
        }
    }
}