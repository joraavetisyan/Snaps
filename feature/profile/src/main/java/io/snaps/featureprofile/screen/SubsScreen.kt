@file:OptIn(ExperimentalFoundationApi::class)

package io.snaps.featureprofile.screen

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.transform.CircleCropTransformation
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

@Composable
fun SubsScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<SubsViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    SubsScreen(
        uiState = uiState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubsScreen(
    uiState: SubsViewModel.UiState,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SimpleTopAppBar(
                title = "@name".textValue(),
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
                .inset(insetAllExcludeTop()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var isFirst by remember { mutableStateOf(true) }
            val subscriptions = StringKey.SubsActionSubscriptions.textValue("26,3k")
            val subscribers = StringKey.SubsActionSubscriptions.textValue("32,6k")
            TitleSlider(title1 = subscriptions, title2 = subscribers, isFirst = isFirst) {
                isFirst = !it
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (isFirst) {
                    items(uiState.subscriptions) { Item(it) }
                } else {
                    items(uiState.subscribers) { Item(it) }
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

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
    SubsScreen(uiState = SubsViewModel.UiState())
}