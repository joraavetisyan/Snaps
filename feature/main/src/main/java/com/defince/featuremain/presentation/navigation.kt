package com.defince.featuremain.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.defince.corenavigation.AppRoute
import com.defince.corenavigation.MainFeatureProvider
import com.defince.corenavigation.base.Navigator
import com.defince.corenavigation.base.composable
import com.defince.corenavigation.base.navigate
import com.defince.featuremain.presentation.screen.MainAScreen
import com.defince.featuremain.presentation.screen.MainBScreen
import com.defince.featuremain.presentation.screen.Mock2Screen
import com.defince.featuremain.presentation.screen.MockScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) : Navigator(navHostController) {

    fun toMock1SecondScreen() = navHostController.navigate(AppRoute.MainBottomBar.Mock1Second)

    fun toMock2SecondScreen() = navHostController.navigate(AppRoute.MainBottomBar.Mock2Second)

    fun toMock3SecondScreen() = navHostController.navigate(AppRoute.MainBottomBar.Mock3Second)
}

class MainFeatureProviderImpl @Inject constructor() : MainFeatureProvider {

    override fun NavGraphBuilder.mock1Graph(controller: NavHostController) {
        composable(AppRoute.MainBottomBar.Mock1) { MainAScreen(controller) }
        composable(AppRoute.MainBottomBar.Mock1Second) { MainBScreen(controller) }
    }

    override fun NavGraphBuilder.mock2Graph(controller: NavHostController) {
        composable(AppRoute.MainBottomBar.Mock2) { MockScreen(controller) }
        composable(AppRoute.MainBottomBar.Mock2Second) { MainBScreen(controller) }
    }

    override fun NavGraphBuilder.mock3Graph(controller: NavHostController) {
        composable(AppRoute.MainBottomBar.Mock3) { Mock2Screen(controller) }
        composable(AppRoute.MainBottomBar.Mock3Second) { MainBScreen(controller) }
    }

    override fun NavGraphBuilder.mock4Graph(controller: NavHostController) {
        composable(AppRoute.MainBottomBar.Mock4) { Mock2Screen(controller) }
        composable(AppRoute.MainBottomBar.Mock4Second) { MainBScreen(controller) }
    }

    override fun NavGraphBuilder.mock5Graph(controller: NavHostController) {
        composable(AppRoute.MainBottomBar.Mock5) { Mock2Screen(controller) }
        composable(AppRoute.MainBottomBar.Mock5Second) { MainBScreen(controller) }
    }
}