package io.snaps.android.mainscreen

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.BottomBarFeatureProvider
import io.snaps.corenavigation.MainFeatureProvider
import io.snaps.corenavigation.RegistrationFeatureProvider
import io.snaps.corenavigation.base.createRoute
import io.snaps.coreuitheme.compose.AppTheme
import javax.inject.Inject

class NavHostProvider @Inject constructor(
    private val registrationFeatureProvider: RegistrationFeatureProvider,
    private val bottomBarFeatureProvider: BottomBarFeatureProvider,
    private val mainFeatureProvider: MainFeatureProvider,
) {

    @Composable
    fun RegistrationNavHost(navController: NavHostController, needOnBoarding: Boolean) = Graph(
        navController, when {
            needOnBoarding -> createRoute(AppRoute.Registration)
            else -> createRoute(AppRoute.Registration)
        }
    )

    @Composable
    fun AuthorizedGraph(navController: NavHostController) =
        Graph(navController, createRoute(AppRoute.MainBottomBar))

    @Composable
    fun Graph(navController: NavHostController, startDestinationRoute: String) {
        NavHost(navController = navController, startDestination = startDestinationRoute) {
            with(registrationFeatureProvider) { registrationGraph(navController) }
            with(mainFeatureProvider) {
                mock1Graph(navController)
                mock2Graph(navController)
                mock3Graph(navController)
                mock4Graph(navController)
                mock5Graph(navController)
            }
            with(bottomBarFeatureProvider) { bottomBarGraph(AppRoute.MainBottomBar, mainBottomBarItems()) }
        }
    }

    private fun mainBottomBarItems() = listOf(
        BottomBarFeatureProvider.ScreenItem(
            icon = AppTheme.specificIcons.camera,
            route = AppRoute.MainBottomBar.MainTab1,
            startDestination = AppRoute.MainBottomBar.Mock1,
            builder = { mainTab1Graph(it) },
        ),
        BottomBarFeatureProvider.ScreenItem(
            icon = AppTheme.specificIcons.star,
            route = AppRoute.MainBottomBar.MainTab2,
            startDestination = AppRoute.MainBottomBar.Mock2,
            builder = { mainTab2Graph(it) },
        ),
        BottomBarFeatureProvider.ScreenItem(
            icon = AppTheme.specificIcons.check,
            route = AppRoute.MainBottomBar.MainTab3,
            startDestination = AppRoute.MainBottomBar.Mock3,
            builder = { mainTab3Graph(it) },
        ),
        BottomBarFeatureProvider.ScreenItem(
            icon = AppTheme.specificIcons.picture,
            route = AppRoute.MainBottomBar.MainTab4,
            startDestination = AppRoute.MainBottomBar.Mock4,
            builder = { mainTab4Graph(it) },
        ),
        BottomBarFeatureProvider.ScreenItem(
            icon = AppTheme.specificIcons.profile,
            route = AppRoute.MainBottomBar.MainTab5,
            startDestination = AppRoute.MainBottomBar.Mock5,
            builder = { mainTab5Graph(it) },
        ),
    )

    private fun NavGraphBuilder.mainTab1Graph(controller: NavHostController) {
        with(mainFeatureProvider) { mock1Graph(controller) }
    }

    private fun NavGraphBuilder.mainTab2Graph(controller: NavHostController) {
        with(mainFeatureProvider) { mock2Graph(controller) }
    }

    private fun NavGraphBuilder.mainTab3Graph(controller: NavHostController) {
        with(mainFeatureProvider) { mock3Graph(controller) }
    }

    private fun NavGraphBuilder.mainTab4Graph(controller: NavHostController) {
        with(mainFeatureProvider) { mock4Graph(controller) }
    }

    private fun NavGraphBuilder.mainTab5Graph(controller: NavHostController) {
        with(mainFeatureProvider) { mock5Graph(controller) }
    }
}