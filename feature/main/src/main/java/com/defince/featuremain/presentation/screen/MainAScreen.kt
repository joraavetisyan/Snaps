package com.defince.featuremain.presentation.screen

import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.defince.corecommon.container.textValue
import com.defince.coreuicompose.tools.add
import com.defince.coreuicompose.tools.insetBottomWithBottomNavigation
import com.defince.coreuicompose.uikit.listtile.CellTileState
import com.defince.coreuicompose.uikit.duplicate.SimpleTopAppBar
import com.defince.featuremain.presentation.ScreenNavigator
import com.defince.featuremain.presentation.viewmodel.AViewModel
import com.defince.featuremain.presentation.viewmodel.CViewModel

@Composable
fun MainAScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<AViewModel>()
    val cviewModel = hiltViewModel<CViewModel>()

    MainAScreen(
        onNextClick = router::toMock1SecondScreen,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainAScreen(
    onNextClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SimpleTopAppBar(
                title = "Main A screen".textValue(),
                scrollBehavior = scrollBehavior,
            )
        }
    ) {
        LazyColumn(
            contentPadding = insetBottomWithBottomNavigation().asPaddingValues().add(it),
        ) {
            items(20) {
                CellTileState.Data(
                    value = "item $it".textValue(),
                    onClick = onNextClick,
                ).Content(modifier = Modifier)
            }
        }
    }
}