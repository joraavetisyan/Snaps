package io.snaps.featurebottombar.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import io.snaps.corecommon.container.IconValue
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.BottomBarFeatureProvider
import io.snaps.coreuicompose.tools.LocalBottomNavigationHeight
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.BottomNavigationBarShape
import io.snaps.featurebottombar.viewmodel.BottomBarViewModel

@Composable
fun BottomBarScreen(
    items: List<BottomBarFeatureProvider.ScreenItem>,
    mainBuilder: NavGraphBuilder.(NavHostController) -> Unit,
) {
    val navController = rememberNavController()
    val viewModel = hiltViewModel<BottomBarViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    val firstDestRoutes = items.map { it.startDestination.path() }

    BackHandler(currentDestination?.route in firstDestRoutes.drop(1)) {
        navController.switchTo(items.first().route.pattern)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CompositionLocalProvider(LocalBottomNavigationHeight provides 80.dp) {
            NavHost(navController = navController, startDestination = items.first().route.pattern) {
                mainBuilder(navController)
                items.forEach {
                    navigation(
                        route = it.route.pattern,
                        startDestination = it.startDestination.pattern,
                    ) {
                        with(it) { builder(navController) }
                    }
                }
            }
            val bottomBarScreens = listOf(
                AppRoute.MainBottomBar.MainTab1Start,
                AppRoute.MainBottomBar.MainTab2Start,
                AppRoute.MainBottomBar.MainTab3Start,
                AppRoute.MainBottomBar.MainTab4Start,
                AppRoute.MainBottomBar.MainTab5Start,
            )
            val bottomBarDestination = bottomBarScreens.any {
                it.path() == currentDestination?.route?.substringBefore(delimiter = "?")
            }
            if (uiState.isBottomBarVisible && bottomBarDestination) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 20.dp)
                        .shadow(elevation = 16.dp, shape = BottomNavigationBarShape)
                        .background(
                            color = AppTheme.specificColorScheme.white,
                            shape = BottomNavigationBarShape,
                        )
                        .padding(horizontal = 12.dp, vertical = 16.dp),
                ) {
                    items.forEach { screen ->
                        val isSelected =
                            currentDestination?.hierarchy?.any { it.route == screen.route.path() } == true
                        MenuItem(
                            icon = screen.icon,
                            color = if (isSelected) {
                                AppTheme.specificColorScheme.uiAccent
                            } else {
                                AppTheme.specificColorScheme.darkGrey
                            },
                            onClick = {
                                navController.switchTo(screen.route.path())
                            },
                        )
                    }
                }
            }
        }
    }
}

private fun NavController.switchTo(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        restoreState = true
        launchSingleTop = true
    }
}

@Composable
private fun MenuItem(
    modifier: Modifier = Modifier,
    icon: IconValue,
    color: Color,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
    ) {
        Icon(
            painter = icon.get(),
            contentDescription = null,
            tint = color,
            modifier = modifier.size(40.dp),
        )
    }
}