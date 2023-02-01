package io.snaps.featuremain.presentation.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavHostController
import io.snaps.corecommon.container.textValue
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.featuremain.presentation.ScreenNavigator

@Composable
fun MockScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    MockScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MockScreen() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SimpleTopAppBar(
                title = "Mock screen".textValue(),
                scrollBehavior = scrollBehavior,
            )
        }
    ) { it }
}