package io.snaps.featurebottombar.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import io.snaps.basesession.data.OnboardingHandler
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.OnboardingType
import io.snaps.corecommon.strings.StringKey
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.BottomBarFeatureProvider
import io.snaps.corenavigation.base.navigate
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.LocalBottomNavigationHeight
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.bottomsheetdialog.OnboardingBottomDialog
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
import io.snaps.featurebottombar.viewmodel.BottomBarViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomBarScreen(
    items: List<BottomBarFeatureProvider.ScreenItem>,
    mainBuilder: NavGraphBuilder.(NavHostController) -> Unit,
) {
    val navController = rememberNavController()
    val viewModel = hiltViewModel<BottomBarViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    viewModel.command.collectAsCommand {
        when (it) {
            BottomBarViewModel.Command.OpenNftPurchaseScreen -> navController.navigate(AppRoute.RankSelection)
        }
    }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    val firstDestRoutes = items.map { it.startDestination.path() }

    BackHandler(currentDestination?.route in firstDestRoutes.drop(1)) {
        navController.switchTo(items.first().route.pattern)
    }

    viewModel.updateMenuRoute(currentBackStackEntry?.destination?.route)

    val coroutineScope = rememberCoroutineScope()
    val onboardingState by viewModel.onboardingUiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    viewModel.onboardingCommand.collectAsCommand {
        when (it) {
            is OnboardingHandler.Command.OpenDialog -> coroutineScope.launch { sheetState.show() }
            OnboardingHandler.Command.HideDialog -> coroutineScope.launch { sheetState.hide() }
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            OnboardingDialog(
                onboardingState = onboardingState,
                onClicked = viewModel::onOnboardingDialogActionClicked,
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            CompositionLocalProvider(LocalBottomNavigationHeight provides 80.dp) {
                NavHost(
                    navController = navController,
                    startDestination = items.first().route.pattern,
                ) {
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
                val currentRoute = currentDestination?.route?.substringBefore(delimiter = "?")
                val bottomBarDestination = bottomBarScreens.any {
                    it.path() == currentRoute
                }
                if (uiState.isBottomBarVisible && bottomBarDestination) {
                    val containerColor = when (currentRoute) {
                        AppRoute.MainBottomBar.MainTab1Start.path() -> AppTheme.specificColorScheme.black
                        else -> AppTheme.specificColorScheme.white
                    }
                    NavigationBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        containerColor = containerColor,
                    ) {
                        items.forEach {
                            MenuItem(
                                navController = navController,
                                uiState = uiState,
                                screenItem = it,
                                currentDestination = currentDestination,
                                containerColor = containerColor,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun RowScope.MenuItem(
    navController: NavHostController,
    uiState: BottomBarViewModel.UiState,
    screenItem: BottomBarFeatureProvider.ScreenItem,
    currentDestination: NavDestination?,
    containerColor: Color,
) {
    val isSelected = currentDestination?.hierarchy?.any {
        it.route == screenItem.route.path()
    } == true
    val badgeText = when (screenItem.route.path()) {
        AppRoute.MainBottomBar.MainTab4.path() -> uiState.badgeText
        else -> null
    }
    NavigationBarItem(
        icon = {
            BadgedBox(
                badge = {
                    if (!badgeText.isNullOrEmpty()) {
                        Badge { Text(badgeText) }
                    }
                }
            ) {
                Icon(
                    painter = screenItem.icon.get(),
                    contentDescription = null,
                )
            }
        },
        label = {
            screenItem.labelKey?.let {
                Text(
                    text = LocalStringHolder.current(it),
                )
            }
        },
        selected = isSelected,
        onClick = { navController.switchTo(screenItem.route.path()) },
        colors = NavigationBarItemDefaults.colors(
            unselectedIconColor = AppTheme.specificColorScheme.darkGrey,
            unselectedTextColor = AppTheme.specificColorScheme.textSecondary,
            selectedTextColor = AppTheme.specificColorScheme.uiAccent,
            indicatorColor = containerColor,
        ),
    )
}

@Composable
private fun OnboardingDialog(
    onboardingState: OnboardingHandler.UiState,
    onClicked: (OnboardingType?) -> Unit,
) {
    when (onboardingState.dialogType) {
        OnboardingType.Rank -> OnboardingBottomDialog(
            image = ImageValue.ResImage(R.drawable.img_direct_referral_2),
            title = StringKey.OnboardingRankTitle.textValue(),
            text = StringKey.OnboardingRankText.textValue(),
            buttonText = StringKey.OnboardingRankAction.textValue(),
            onClick = {
                onClicked(onboardingState.dialogType)
            },
        )
        OnboardingType.Popular -> OnboardingBottomDialog(
            image = ImageValue.ResImage(R.drawable.img_direct_referral_2),
            title = StringKey.OnboardingPopularTitle.textValue(),
            text = StringKey.OnboardingPopularText.textValue(),
            buttonText = StringKey.OnboardingPopularAction.textValue(),
            onClick = {
                onClicked(onboardingState.dialogType)
            },
        )
        OnboardingType.Tasks -> OnboardingBottomDialog(
            image = ImageValue.ResImage(R.drawable.img_direct_referral_2),
            title = StringKey.OnboardingTasksTitle.textValue(),
            text = StringKey.OnboardingTasksText.textValue(),
            buttonText = StringKey.OnboardingTasksAction.textValue(),
            onClick = {
                onClicked(onboardingState.dialogType)
            },
        )
        OnboardingType.Nft -> OnboardingBottomDialog(
            image = ImageValue.ResImage(R.drawable.img_direct_referral_2),
            title = StringKey.OnboardingNftTitle.textValue(),
            text = StringKey.OnboardingNftText.textValue(),
            buttonText = StringKey.OnboardingNftAction.textValue(),
            onClick = {
                onClicked(onboardingState.dialogType)
            },
        )
        OnboardingType.Referral -> OnboardingBottomDialog(
            image = ImageValue.ResImage(R.drawable.img_direct_referral_2),
            title = StringKey.OnboardingReferralTitle.textValue(),
            text = StringKey.OnboardingReferralText.textValue(),
            buttonText = StringKey.OnboardingReferralAction.textValue(),
            onClick = {
                onClicked(onboardingState.dialogType)
            },
        )
        OnboardingType.Wallet -> OnboardingBottomDialog(
            image = ImageValue.ResImage(R.drawable.img_direct_referral_2),
            title = StringKey.OnboardingWalletTitle.textValue(),
            text = StringKey.OnboardingWalletText.textValue(),
            buttonText = StringKey.OnboardingWalletAction.textValue(),
            onClick = {
                onClicked(onboardingState.dialogType)
            },
        )
        OnboardingType.Rewards -> OnboardingBottomDialog(
            image = ImageValue.ResImage(R.drawable.img_direct_referral_2),
            title = StringKey.OnboardingRewardsTitle.textValue(),
            text = StringKey.OnboardingRewardsText.textValue(),
            buttonText = StringKey.OnboardingRewardsAction.textValue(),
            onClick = {
                onClicked(onboardingState.dialogType)
            },
        )
        null -> Box(modifier = Modifier.size(1.dp))
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