package io.snaps.featurewallet.screen

import android.app.Activity
import android.graphics.Bitmap
import android.net.http.SslError
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.basewallet.data.blockchain.trustwallet.TrustWalletProvider
import io.snaps.basewallet.data.blockchain.trustwallet.TrustWalletWebAppInterface
import io.snaps.basewallet.domain.SwapTransactionModel
import io.snaps.basewallet.ui.TransferTokensDialogHandler
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.corenavigation.base.openUrl
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuicompose.uikit.status.FullScreenLoaderUi
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featurewallet.ScreenNavigator
import io.snaps.featurewallet.viewmodel.ExchangeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExchangeScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<ExchangeViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    val transferTokensState by viewModel.transferTokensState.collectAsState()

    viewModel.command.collectAsCommand {
        when (it) {
            ExchangeViewModel.Command.CloseScreen -> router.back()
        }
    }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    val context = LocalContext.current

    viewModel.transferTokensCommand.collectAsCommand {
        when (it) {
            TransferTokensDialogHandler.Command.ShowBottomDialog -> coroutineScope.launch { sheetState.show() }
            TransferTokensDialogHandler.Command.HideBottomDialog -> coroutineScope.launch { sheetState.hide() }
        }
    }
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            when (val dialog = transferTokensState.bottomDialog) {
                is TransferTokensDialogHandler.BottomDialog.TokensTransferSuccess -> TokensTransferSuccessDialog(
                    dialog = dialog,
                    onClick = {
                        coroutineScope.launch { sheetState.hide() }
                        context.openUrl(dialog.bscScanLink)
                    }
                )
                else -> Unit
            }
        }
    ) {
        ExchangeScreen(
            uiState = uiState,
            onBackClicked = router::back,
            onTransactionSendClicked = viewModel::onTransactionSendClicked,
        )
    }

    FullScreenLoaderUi(isLoading = uiState.isLoading)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExchangeScreen(
    uiState: ExchangeViewModel.UiState,
    onBackClicked: () -> Boolean,
    onTransactionSendClicked: (SwapTransactionModel) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val localContext = LocalContext.current
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = AppTheme.specificColorScheme.uiContentBg,
        topBar = {
            SimpleTopAppBar(
                title = StringKey.ExchangeTitle.textValue(),
                navigationIcon = AppTheme.specificIcons.back to onBackClicked,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        AndroidView(
            modifier = Modifier.padding(paddingValues),
            factory = { context ->
                val address = uiState.walletModel.receiveAddress
                /*WebView.setWebContentsDebuggingEnabled(true)*/
                val webView = WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
                val webAppInterface = TrustWalletWebAppInterface(
                    address = address,
                    webView = webView,
                    sendTransaction = onTransactionSendClicked,
                )
                webView.addJavascriptInterface(webAppInterface, TrustWalletWebAppInterface.name)
                webView.settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                }
                val providerJs = TrustWalletProvider.loadProviderJs(localContext as Activity)
                val initJs = TrustWalletProvider.loadInitJs(address)
                val webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        view?.evaluateJavascript(providerJs, null)
                        view?.evaluateJavascript(initJs, null)
                    }

                    override fun onReceivedSslError(
                        view: WebView?,
                        handler: SslErrorHandler?,
                        error: SslError?
                    ) {
                        // Ignore SSL certificate errors
                        // handler?.proceed()
                    }
                }
                webView.webViewClient = webViewClient
                webView.loadUrl(TrustWalletProvider.swapProvideUrl(address = uiState.walletModel.coinType.address))
                webView
            }
        )
    }
}