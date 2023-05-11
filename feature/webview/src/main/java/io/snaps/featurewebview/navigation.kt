package io.snaps.featurewebview

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.WebViewFeatureProvider
import io.snaps.corenavigation.base.Navigator
import io.snaps.corenavigation.base.composable
import io.snaps.featurewebview.presentation.screen.WebViewScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) : Navigator(navHostController)

class WebViewFeatureProviderImpl @Inject constructor() : WebViewFeatureProvider {

    override fun NavGraphBuilder.webViewGraph(controller: NavHostController) {
        composable(AppRoute.WebView) { WebViewScreen(controller) }
    }
}