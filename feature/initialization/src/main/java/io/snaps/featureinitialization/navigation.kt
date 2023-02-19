package io.snaps.featureinitialization

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.InitializationFeatureProvider
import io.snaps.corenavigation.base.Navigator
import io.snaps.corenavigation.base.composable
import io.snaps.corenavigation.base.navigate
import io.snaps.featureinitialization.presentation.screen.CreateUserScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) :
    Navigator(navHostController) {

    fun toRankSelectionScreen() = navHostController.navigate(AppRoute.RankSelection)

    fun toMainScreen() = navHostController.navigate(AppRoute.MainBottomBar)
}

class InitializationFeatureProviderImpl @Inject constructor() : InitializationFeatureProvider {

    override fun NavGraphBuilder.initializationGraph(controller: NavHostController) {
        composable(AppRoute.UserCreate) { CreateUserScreen(controller) }
    }
}