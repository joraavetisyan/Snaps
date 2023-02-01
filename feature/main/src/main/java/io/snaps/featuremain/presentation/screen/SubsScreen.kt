@file:OptIn(ExperimentalFoundationApi::class)

package io.snaps.featuremain.presentation.screen

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.transform.CircleCropTransformation
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonLightM
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.LeftPart
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.MainHeaderElementShape
import io.snaps.featuremain.domain.Sub
import io.snaps.featuremain.presentation.ScreenNavigator
import io.snaps.featuremain.presentation.viewmodel.SubsViewModel

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
        ) {
            var isFirst by remember { mutableStateOf(true) }
            Card(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
                    .shadow(elevation = 16.dp, shape = MainHeaderElementShape)
                    .background(
                        color = AppTheme.specificColorScheme.uiContentBg,
                        shape = MainHeaderElementShape,
                    ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val onClick = { isFirst = !isFirst }

                    @Composable
                    fun Active(text: TextValue) = SimpleButtonLightM(onClick = onClick) {
                        SimpleButtonContent(text = text)
                    }

                    @Composable
                    fun NotActive(text: TextValue) = Text(
                        text = text.get(),
                        modifier = Modifier.defaultTileRipple(onClick = onClick),
                    )

                    val subscriptions = StringKey.SubsActionSubscriptions.textValue("26,3k")
                    if (isFirst) Active(text = subscriptions) else NotActive(text = subscriptions)
                    val subscribers = StringKey.SubsActionSubscriptions.textValue("32,6k")
                    if (!isFirst) Active(text = subscribers) else NotActive(text = subscribers)
                }
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