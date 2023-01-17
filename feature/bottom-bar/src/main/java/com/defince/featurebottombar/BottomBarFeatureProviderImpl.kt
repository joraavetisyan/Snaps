package com.defince.featurebottombar

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.defince.corenavigation.BottomBarFeatureProvider
import com.defince.corenavigation.Route
import com.defince.corenavigation.base.composable
import javax.inject.Inject

class BottomBarFeatureProviderImpl @Inject constructor() : BottomBarFeatureProvider {

    override fun NavGraphBuilder.bottomBarGraph(route: Route, items: List<BottomBarFeatureProvider.ScreenItem>) {
        composable(route) { BottomBarScreen(items) }
    }
}