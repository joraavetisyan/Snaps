package io.snaps.featureprofile.presentation.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.corecommon.strings.StringKey
import io.snaps.corenavigation.base.openUrl
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
import io.snaps.featureprofile.ScreenNavigator
import io.snaps.featureprofile.presentation.viewmodel.SocialNetworksViewModel

@Composable
fun SocialNetworksScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<SocialNetworksViewModel>()

    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()
    viewModel.command.collectAsCommand {
        when (it) {
            is SocialNetworksViewModel.Command.OpenLink -> { context.openUrl(it.link) }
        }
    }

    SocialNetworksScreen(
        uiState = uiState,
        onBackClicked = router::back,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SocialNetworksScreen(
    uiState: SocialNetworksViewModel.UiState,
    onBackClicked: () -> Boolean,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = AppTheme.specificColorScheme.uiContentBg,
        topBar = {
            SimpleTopAppBar(
                title = {
                    Text(text = LocalStringHolder.current(StringKey.SocialNetworksTitle))
                },
                navigationIcon = AppTheme.specificIcons.back to onBackClicked,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
            modifier = Modifier
                .padding(paddingValues)
                .inset(insetAllExcludeTop()),
        ) {
            items(uiState.items) { item ->
                item.Content(
                    modifier = Modifier
                        .shadow(
                            shape = AppTheme.shapes.medium,
                            elevation = 16.dp
                        )
                        .background(
                            color = AppTheme.specificColorScheme.white,
                            shape = AppTheme.shapes.medium,
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                )
            }
        }
    }
}