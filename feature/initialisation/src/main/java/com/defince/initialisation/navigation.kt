package com.defince.initialisation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.defince.corenavigation.AppRoute
import com.defince.corenavigation.InitializationFeatureProvider
import com.defince.corenavigation.base.Navigator
import com.defince.corenavigation.base.composable
import com.defince.initialisation.screen.CreateUserScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) : Navigator(navHostController)

class InitializationFeatureProviderImpl @Inject constructor() : InitializationFeatureProvider {

    override fun NavGraphBuilder.initializationGraph(controller: NavHostController) {
        composable(AppRoute.CreateUser) { CreateUserScreen(controller) }
    }
}