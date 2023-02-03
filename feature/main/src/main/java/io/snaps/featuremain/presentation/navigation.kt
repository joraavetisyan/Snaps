package io.snaps.featuremain.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.MainFeatureProvider
import io.snaps.corenavigation.base.Navigator
import io.snaps.corenavigation.base.composable
import io.snaps.corenavigation.base.navigate
import io.snaps.featuremain.presentation.screen.ItemListScreen
import io.snaps.featuremain.presentation.screen.PopularVideosScreen
import io.snaps.featuremain.presentation.screen.ProfileScreen
import io.snaps.featuremain.presentation.screen.RankSelectionScreen
import io.snaps.featuremain.presentation.screen.ReferralProgramScreen
import io.snaps.featuremain.presentation.screen.SubsScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) : Navigator(navHostController) {

    fun toReferralProgramScreen() = navHostController.navigate(AppRoute.MainBottomBar.ReferralProgramScreen)
}

class MainFeatureProviderImpl @Inject constructor() : MainFeatureProvider {

    override fun NavGraphBuilder.mock1Graph(controller: NavHostController) {
        composable(AppRoute.MainBottomBar.Mock1) { PopularVideosScreen(controller) }
    }

    override fun NavGraphBuilder.mock2Graph(controller: NavHostController) {
        composable(AppRoute.MainBottomBar.Mock2) { RankSelectionScreen(controller) }
    }

    override fun NavGraphBuilder.mock3Graph(controller: NavHostController) {
        composable(AppRoute.MainBottomBar.Mock3) { SubsScreen(controller) }
    }

    override fun NavGraphBuilder.mock4Graph(controller: NavHostController) {
        composable(AppRoute.MainBottomBar.Mock4) { ItemListScreen(controller) }
    }

    override fun NavGraphBuilder.mock5Graph(controller: NavHostController) {
        composable(AppRoute.MainBottomBar.Mock5) { ProfileScreen(controller) }
        composable(AppRoute.MainBottomBar.ReferralProgramScreen) { ReferralProgramScreen(controller) }
    }
}