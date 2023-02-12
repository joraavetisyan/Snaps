package io.snaps.featurebottombar

import androidx.navigation.NavGraphBuilder
import io.snaps.corenavigation.BottomBarFeatureProvider
import io.snaps.corenavigation.Route
import io.snaps.corenavigation.base.composable
import io.snaps.featurebottombar.screen.BottomBarScreen
import javax.inject.Inject

class BottomBarFeatureProviderImpl @Inject constructor() : BottomBarFeatureProvider {

    override fun NavGraphBuilder.bottomBarGraph(route: Route, items: List<BottomBarFeatureProvider.ScreenItem>) {
        composable(route) { BottomBarScreen(items) }
    }
}