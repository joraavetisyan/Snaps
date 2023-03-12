package io.snaps.featurecollection.presentation.screen

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.corecommon.container.textValue
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAll
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionL
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featurecollection.ScreenNavigator
import io.snaps.featurecollection.presentation.viewmodel.BuyNftViewModel

@Composable
fun BuyNftScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<BuyNftViewModel>()
    val context = LocalContext.current

    viewModel.command.collectAsCommand {}

    BuyNftScreen(
        onBuyClicked = { viewModel.onBuyClicked(context as Activity) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BuyNftScreen(
    onBuyClicked: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {},
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .background(color = AppTheme.specificColorScheme.uiContentBg)
                .padding(paddingValues)
                .inset(insetAll()),
        ) {
            SimpleButtonActionL(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = onBuyClicked,
            ) {
                SimpleButtonContent(text = "Buy".textValue())
            }
        }
    }
}