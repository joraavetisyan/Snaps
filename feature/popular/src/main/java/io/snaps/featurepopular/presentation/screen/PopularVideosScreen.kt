package io.snaps.featurepopular.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.basefeed.ui.VideoFeedGrid
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.ui.MainHeader
import io.snaps.baseprofile.ui.MainHeaderState
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAll
import io.snaps.coreuicompose.uikit.input.SimpleTextField
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featurepopular.ScreenNavigator
import io.snaps.featurepopular.presentation.viewmodel.PopularVideosViewModel

@Composable
fun PopularVideosScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<PopularVideosViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    val headerState by viewModel.headerUiState.collectAsState()

    viewModel.command.collectAsCommand {
        when (it) {
            is PopularVideosViewModel.Command.OpenPopularVideoFeedScreen -> {
                router.toPopularVideoFeedScreen(query = it.query, position = it.position)
            }
        }
    }

    viewModel.headerCommand.collectAsCommand {
        when (it) {
            MainHeaderHandler.Command.OpenProfileScreen -> router.toProfileScreen()
            MainHeaderHandler.Command.OpenWalletScreen -> { /*todo*/ }
        }
    }

    PopularVideosScreen(
        uiState = uiState,
        headerState = headerState.value,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onClick = viewModel::onItemClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PopularVideosScreen(
    uiState: PopularVideosViewModel.UiState,
    headerState: MainHeaderState,
    onSearchQueryChanged: (String) -> Unit,
    onClick: (Int) -> Unit,
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
            MainHeader(state = headerState)
            Text(
                text = StringKey.PopularVideosTitle.textValue().get(),
                style = AppTheme.specificTypography.titleLarge,
                modifier = Modifier.padding(12.dp),
            )
            SimpleTextField(value = uiState.query,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                placeholder = {
                    Text(text = StringKey.PopularVideosHint.textValue().get())
                },
                trailingIcon = {
                    Icon(
                        painter = AppTheme.specificIcons.search.get(),
                        contentDescription = null,
                        tint = AppTheme.specificColorScheme.darkGrey,
                    )
                })
            VideoFeedGrid(
                columnCount = 2,
                uiState = uiState.videoFeedUiState,
                onClick = onClick,
            )
        }
    }
}