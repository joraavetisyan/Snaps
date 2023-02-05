package io.snaps.featurefeed.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.baseprofile.ui.MainHeader
import io.snaps.baseprofile.ui.MainHeaderState
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAll
import io.snaps.coreuicompose.uikit.input.SimpleTextField
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.baseplayer.domain.Reel
import io.snaps.featurefeed.viewmodel.PopularVideosViewModel
import io.snaps.baseplayer.ui.ReelPlayer
import io.snaps.featurefeed.ScreenNavigator

@Composable
fun PopularVideosScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<PopularVideosViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    val headerState by viewModel.headerState.collectAsState()

    PopularVideosScreen(
        uiState = uiState,
        headerState = headerState.value,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PopularVideosScreen(
    uiState: PopularVideosViewModel.UiState,
    headerState: MainHeaderState,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {},
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .inset(insetAll()),
        ) {
            MainHeader(uiState = headerState)
            Text(
                text = StringKey.PopularVideosTitle.textValue().get(),
                style = AppTheme.specificTypography.titleLarge,
                modifier = Modifier.padding(12.dp),
            )
            SimpleTextField(value = "", onValueChange = {})
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(12.dp),
            ) {
                itemsIndexed(
                    items = uiState.reels,
                    key = { _, item -> item.reelInfo.id },
                ) { index, it ->
                    Item(it, index == 0)
                }
            }
        }
    }
}

@Composable
private fun Item(item: Reel, shouldPlay: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(177f / 222f)
            .shadow(elevation = 16.dp, shape = AppTheme.shapes.medium)
            .background(
                color = AppTheme.specificColorScheme.uiContentBg,
                shape = AppTheme.shapes.medium,
            ),
    ) {
        ReelPlayer(
            reel = item,
            shouldPlay = shouldPlay,
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 4.dp, vertical = 8.dp),
        ) {
            Icon(AppTheme.specificIcons.play.get(), null, tint = AppTheme.specificColorScheme.white)
            Text(item.reelInfo.likes.toString(), color = AppTheme.specificColorScheme.white)
        }
    }
}