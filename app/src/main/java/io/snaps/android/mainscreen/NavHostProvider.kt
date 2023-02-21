package io.snaps.android.mainscreen

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.BottomBarFeatureProvider
import io.snaps.corenavigation.CollectionFeatureProvider
import io.snaps.corenavigation.FeedFeatureProvider
import io.snaps.corenavigation.InitializationFeatureProvider
import io.snaps.corenavigation.PopularFeatureProvider
import io.snaps.corenavigation.ProfileFeatureProvider
import io.snaps.corenavigation.RegistrationFeatureProvider
import io.snaps.corenavigation.TasksFeatureProvider
import io.snaps.corenavigation.WalletConnectFeatureProvider
import io.snaps.corenavigation.WalletFeatureProvider
import io.snaps.corenavigation.base.createRoute
import io.snaps.coreuitheme.compose.AppTheme
import javax.inject.Inject

class NavHostProvider @Inject constructor(
    private val registrationFeatureProvider: RegistrationFeatureProvider,
    private val walletConnectFeatureProvider: WalletConnectFeatureProvider,
    private val initializationFeatureProvider: InitializationFeatureProvider,
    private val bottomBarFeatureProvider: BottomBarFeatureProvider,
    private val feedFeatureProvider: FeedFeatureProvider,
    private val popularFeatureProvider: PopularFeatureProvider,
    private val tasksFeatureProvider: TasksFeatureProvider,
    private val collectionFeatureProvider: CollectionFeatureProvider,
    private val profileFeatureProvider: ProfileFeatureProvider,
    private val walletFeatureProvider: WalletFeatureProvider,
) {

    @Composable
    fun NonAuthorizedGraph(
        navController: NavHostController,
        needsStartOnBoarding: Boolean,
    ) = Graph(
        navController = navController,
        startDestinationRoute = when {
            needsStartOnBoarding -> createRoute(AppRoute.Registration)
            else -> createRoute(AppRoute.Registration)
        }
    )

    @Composable
    fun AuthorizedGraph(
        navController: NavHostController,
        needsWalletConnect: Boolean,
        needsInitialization: Boolean,
        needsRanking: Boolean,
    ) = Graph(
        navController = navController,
        startDestinationRoute = when {
            needsWalletConnect -> createRoute(AppRoute.WalletConnect)
            needsInitialization -> createRoute(AppRoute.UserCreate)
            needsRanking -> createRoute(AppRoute.RankSelection)
            else -> createRoute(AppRoute.MainBottomBar)
        },
    )

    @Composable
    fun Graph(navController: NavHostController, startDestinationRoute: String) {
        NavHost(navController = navController, startDestination = startDestinationRoute) {
            with(registrationFeatureProvider) { registrationGraph(navController) }
            with(walletConnectFeatureProvider) { walletConnectGraph(navController) }
            with(initializationFeatureProvider) { initializationGraph(navController) }
            with(collectionFeatureProvider) { collectionGraph(navController) }
            with(profileFeatureProvider) { profileGraph(navController) }
            with(bottomBarFeatureProvider) {
                bottomBarGraph(
                    route = AppRoute.MainBottomBar,
                    items = mainBottomBarItems
                ) { controller ->
                    with(walletFeatureProvider) { walletGraph(controller) }
                }
            }
        }
    }

    private val mainBottomBarItems
        get() = listOf(
            BottomBarFeatureProvider.ScreenItem(
                icon = AppTheme.specificIcons.camera,
                route = AppRoute.MainBottomBar.MainTab1,
                startDestination = AppRoute.MainBottomBar.MainTab1Start,
                builder = { mainTab1Graph(it) },
            ),
            BottomBarFeatureProvider.ScreenItem(
                icon = AppTheme.specificIcons.star,
                route = AppRoute.MainBottomBar.MainTab2,
                startDestination = AppRoute.MainBottomBar.MainTab2Start,
                builder = { mainTab2Graph(it) },
            ),
            BottomBarFeatureProvider.ScreenItem(
                icon = AppTheme.specificIcons.check,
                route = AppRoute.MainBottomBar.MainTab3,
                startDestination = AppRoute.MainBottomBar.MainTab3Start,
                builder = { mainTab3Graph(it) },
            ),
            BottomBarFeatureProvider.ScreenItem(
                icon = AppTheme.specificIcons.picture,
                route = AppRoute.MainBottomBar.MainTab4,
                startDestination = AppRoute.MainBottomBar.MainTab4Start,
                builder = { mainTab4Graph(it) },
            ),
            BottomBarFeatureProvider.ScreenItem(
                icon = AppTheme.specificIcons.profile,
                route = AppRoute.MainBottomBar.MainTab5,
                startDestination = AppRoute.MainBottomBar.MainTab5Start,
                builder = { mainTab5Graph(it) },
            ),
        )

    private fun NavGraphBuilder.mainTab1Graph(controller: NavHostController) {
        with(feedFeatureProvider) { feedGraph(controller) }
    }

    private fun NavGraphBuilder.mainTab2Graph(controller: NavHostController) {
        with(popularFeatureProvider) { popularGraph(controller) }
    }

    private fun NavGraphBuilder.mainTab3Graph(controller: NavHostController) {
        with(tasksFeatureProvider) { tasksGraph(controller) }
    }

    private fun NavGraphBuilder.mainTab4Graph(controller: NavHostController) {
        with(collectionFeatureProvider) { collectionGraph(controller) }
    }

    private fun NavGraphBuilder.mainTab5Graph(controller: NavHostController) {
        with(profileFeatureProvider) { profileGraph(controller) }
    }
}