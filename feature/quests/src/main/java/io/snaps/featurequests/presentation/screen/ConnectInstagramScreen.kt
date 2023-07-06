package io.snaps.featurequests.presentation.screen

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.corenavigation.base.popBackStackWithResult
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featurequests.ScreenNavigator
import io.snaps.featurequests.presentation.viewmodel.ConnectInstagramViewModel

@Composable
fun ConnectInstagramScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }

    val viewModel: ConnectInstagramViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    viewModel.command.collectAsCommand {
        when (it) {
            is ConnectInstagramViewModel.Command.CloseScreen -> navHostController.popBackStackWithResult(it.authCode)
        }
    }

    ConnectInstagramScreen(
        uiState = uiState,
        onBackIconClicked = router::back,
        onAuthCodeReceived = viewModel::onAuthCodeReceived,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConnectInstagramScreen(
    uiState: ConnectInstagramViewModel.UiState,
    onBackIconClicked: () -> Boolean,
    onAuthCodeReceived: (String) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SimpleTopAppBar(
                title = null,
                navigationIcon = AppTheme.specificIcons.back to onBackIconClicked,
                scrollBehavior = scrollBehavior,
            )
        }
    ) { paddingValues ->
        AndroidView(
            factory = {
                WebView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = true
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            url?.let {
                                onAuthCodeReceived(url)
                            }
                        }
                    }
                    loadUrl(uiState.url)
                }
            },
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        )
    }
}