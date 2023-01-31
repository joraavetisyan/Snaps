package com.defince.featuremain.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.defince.corenavigation.AppRoute
import com.defince.corenavigation.MainFeatureProvider
import com.defince.corenavigation.base.Navigator
import com.defince.corenavigation.base.composable
import com.defince.featuremain.presentation.screen.ItemListScreen
import com.defince.featuremain.presentation.screen.MockScreen
import com.defince.featuremain.presentation.screen.ProfileScreen
import com.defince.featuremain.presentation.screen.RankSelectionScreen
import com.defince.featuremain.presentation.screen.ReelsScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) : Navigator(navHostController)

class MainFeatureProviderImpl @Inject constructor() : MainFeatureProvider {

    override fun NavGraphBuilder.mock1Graph(controller: NavHostController) {
        composable(AppRoute.MainBottomBar.Mock1) { ReelsScreen(controller) }
    }

    override fun NavGraphBuilder.mock2Graph(controller: NavHostController) {
        composable(AppRoute.MainBottomBar.Mock2) { RankSelectionScreen(controller) }
    }

    override fun NavGraphBuilder.mock3Graph(controller: NavHostController) {
        composable(AppRoute.MainBottomBar.Mock3) { MockScreen(controller) }
    }

    override fun NavGraphBuilder.mock4Graph(controller: NavHostController) {
        composable(AppRoute.MainBottomBar.Mock4) { ItemListScreen(controller) }
    }

    override fun NavGraphBuilder.mock5Graph(controller: NavHostController) {
        composable(AppRoute.MainBottomBar.Mock5) { ProfileScreen(controller) }
    }
}