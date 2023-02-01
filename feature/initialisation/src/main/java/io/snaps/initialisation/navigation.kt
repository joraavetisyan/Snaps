package io.snaps.initialisation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.InitializationFeatureProvider
import io.snaps.corenavigation.base.Navigator
import io.snaps.corenavigation.base.composable
import io.snaps.initialisation.screen.CreateUserScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) : Navigator(navHostController)

class InitializationFeatureProviderImpl @Inject constructor() : InitializationFeatureProvider {

    override fun NavGraphBuilder.initializationGraph(controller: NavHostController) {
        composable(AppRoute.CreateUser) { CreateUserScreen(controller) }
    }
}