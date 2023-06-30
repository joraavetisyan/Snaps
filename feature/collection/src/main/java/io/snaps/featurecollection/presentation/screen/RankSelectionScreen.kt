package io.snaps.featurecollection.presentation.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.corecommon.R
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.bottomsheetdialog.FootnoteBottomDialog
import io.snaps.coreuicompose.uikit.bottomsheetdialog.FootnoteBottomDialogItem
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuicompose.uikit.listtile.HeaderTileState
import io.snaps.coreuicompose.uikit.other.TitleSlider
import io.snaps.coreuicompose.uikit.status.FootnoteUi
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featurecollection.ScreenNavigator
import io.snaps.featurecollection.presentation.viewmodel.RankSelectionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RankSelectionScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<RankSelectionViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    val coroutineScope = rememberCoroutineScope()

    viewModel.command.collectAsCommand {
        when (it) {
            is RankSelectionViewModel.Command.OpenPurchase -> router.toPurchaseScreen(it.args)
            is RankSelectionViewModel.Command.OpenMysteryBox -> router.toMysteryBoxScreen(it.args)
            is RankSelectionViewModel.Command.OpenBundle -> router.toBundleScreen(it.args)
            RankSelectionViewModel.Command.ShowBottomDialog -> coroutineScope.launch { sheetState.show() }
            RankSelectionViewModel.Command.HideBottomDialog -> coroutineScope.launch { sheetState.hide() }
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            when (uiState.bottomDialog) {
                RankSelectionViewModel.BottomDialog.RankFootnote -> FootnoteBottomDialog(
                    FootnoteBottomDialogItem(
                        image = R.drawable.img_guy_eating.imageValue(),
                        title = StringKey.RankSelectionDialogTitleFootnote1.textValue(),
                        text = StringKey.RankSelectionDialogMessageFootnote1.textValue(),
                        onClick = viewModel::onRaiseNftRankClick,
                        buttonText = StringKey.RankSelectionDialogActionFootnote1.textValue(),
                    ),
                )
            }
        }
    ) {
        RankSelectionScreen(
            uiState = uiState,
            coroutineScope = coroutineScope,
            onRankFootnoteClick = viewModel::onRankFootnoteClick,
            onBackClicked = router::back,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun RankSelectionScreen(
    uiState: RankSelectionViewModel.UiState,
    coroutineScope: CoroutineScope,
    onRankFootnoteClick: () -> Unit,
    onBackClicked: () -> Boolean,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val pagerState = rememberPagerState()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SimpleTopAppBar(
                title = StringKey.RankSelectionTitle.textValue(),
                navigationIcon = AppTheme.specificIcons.back to onBackClicked,
                scrollBehavior = scrollBehavior,
            ) {
                if (uiState.isBundleEnabled) {
                    TitleSlider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        items = listOf(
                            StringKey.RankSelectionTitleSliderBundles.textValue(),
                            StringKey.RankSelectionTitleSliderNft.textValue(),
                        ),
                        selectedItemIndex = pagerState.currentPage,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(it)
                            }
                        },
                    )
                }
            }
        },
    ) { paddingValues ->
        if (uiState.isBundleEnabled) {
            HorizontalPager(
                pageCount = 2,
                state = pagerState,
            ) {
                RankSelectionContent(
                    modifier = Modifier.padding(paddingValues),
                ) {
                    when (it) {
                        0 -> {
                            bundles(bundles = uiState.bundles)
                        }
                        1 -> {
                            nftItems(
                                isMysteryBoxEnabled = uiState.isMysteryBoxEnabled,
                                mysteryBoxes = uiState.mysteryBoxes,
                                ranks = uiState.ranks,
                                onRankFootnoteClick = onRankFootnoteClick,
                            )
                        }
                    }
                }
            }
        } else {
            RankSelectionContent(
                modifier = Modifier.padding(paddingValues),
            ) {
                nftItems(
                    isMysteryBoxEnabled = uiState.isMysteryBoxEnabled,
                    mysteryBoxes = uiState.mysteryBoxes,
                    ranks = uiState.ranks,
                    onRankFootnoteClick = onRankFootnoteClick,
                )
            }
        }
    }
}

@Composable
private fun RankSelectionContent(
    modifier: Modifier,
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        modifier = modifier.inset(insetAllExcludeTop()),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}

private fun LazyListScope.nftItems(
    isMysteryBoxEnabled: Boolean,
    mysteryBoxes: List<MysteryBoxTileState>,
    ranks: List<RankTileState>,
    onRankFootnoteClick: () -> Unit,
) {
    if (isMysteryBoxEnabled) {
        item {
            HeaderTileState.small(StringKey.RankSelectionTitleMysteryBox.textValue()).Content(
                modifier = Modifier
            )
        }
        items(mysteryBoxes) { it.Content(modifier = Modifier) }
    }
    item {
        HeaderTileState.small(StringKey.RankSelectionTitleNft.textValue()).Content(
            modifier = Modifier
        )
        FootnoteUi(
            action = StringKey.RankSelectionActionFootnote.textValue(),
            onClick = onRankFootnoteClick,
            padding = 0.dp,
        )
    }
    items(ranks) { it.Content(modifier = Modifier) }
}

private fun LazyListScope.bundles(
    bundles: List<BundleTileState>,
) {
    item {
        HeaderTileState
            .small(StringKey.RankSelectionTitleSliderBundles.textValue())
            .Content(modifier = Modifier.padding(bottom = 4.dp))
    }
    items(bundles) { it.Content(modifier = Modifier) }
}