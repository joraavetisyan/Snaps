package io.snaps.featurecollection.presentation.screen

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.ui.MainHeader
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAll
import io.snaps.coreuicompose.uikit.status.FullScreenLoaderUi
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
import io.snaps.featurecollection.ScreenNavigator
import io.snaps.featurecollection.presentation.viewmodel.MyCollectionViewModel

@Composable
fun MyCollectionScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<MyCollectionViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    val headerState by viewModel.headerUiState.collectAsState()

    viewModel.headerCommand.collectAsCommand {
        when (it) {
            MainHeaderHandler.Command.OpenProfileScreen -> router.toProfileScreen()
            MainHeaderHandler.Command.OpenWalletScreen -> router.toWalletScreen()
        }
    }

    viewModel.command.collectAsCommand {
        when (it) {
            is MyCollectionViewModel.Command.OpenRankSelectionScreen -> router.toRankSelectionScreen()
        }
    }

    MyCollectionScreen(
        uiState = uiState,
        headerState = headerState,
    )

    FullScreenLoaderUi(isLoading = uiState.isLoading)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyCollectionScreen(
    uiState: MyCollectionViewModel.UiState,
    headerState: MainHeaderHandler.UiState,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {},
    ) {
        Column(
            modifier = Modifier
                .background(color = AppTheme.specificColorScheme.uiContentBg)
                .padding(it)
                .inset(insetAll()),
        ) {
            MainHeader(state = headerState.value)
            Header()
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(
                    top = 12.dp,
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 100.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(uiState.nft) {
                    it.Content(modifier = Modifier)
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