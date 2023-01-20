package com.defince.featurebottombar

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.defince.corecommon.container.IconValue
import com.defince.corenavigation.BottomBarFeatureProvider
import com.defince.coreuicompose.tools.LocalBottomNavigationHeight
import com.defince.coreuicompose.tools.get
import com.defince.coreuitheme.compose.AppTheme
import com.defince.coreuitheme.compose.BottomNavigationBarShape

@Composable
fun BottomBarScreen(
    items: List<BottomBarFeatureProvider.ScreenItem>,
) {
    val navController = rememberNavController()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    val firstDestRoutes = items.map { it.startDestination.path() }

    BackHandler(currentDestination?.route in firstDestRoutes.drop(1)) {
        navController.switchTo(items.first().route.pattern)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CompositionLocalProvider(LocalBottomNavigationHeight provides 80.dp) {
            NavHost(navController = navController, startDestination = items.first().route.pattern) {
                items.forEach {
                    navigation(route = it.route.pattern, startDestination = it.startDestination.pattern) {
                        with(it) { builder(navController) }
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 20.dp)
                    .shadow(elevation = 16.dp, shape = BottomNavigationBarShape)
                    .background(color = AppTheme.specificColorScheme.surface, shape = BottomNavigationBarShape)
                    .padding(horizontal = 12.dp, vertical = 16.dp),
            ) {
                items.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route.path() } == true
                    MenuItem(
                        icon = screen.icon,
                        color = if (isSelected) {
                            AppTheme.specificColorScheme.onSecondaryContainer
                        } else {
                            AppTheme.specificColorScheme.onSurfaceVariant
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