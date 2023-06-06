package io.snaps.featurebottombar.screen

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
import io.snaps.baseprofile.data.model.BannerDto
import io.snaps.basesession.data.OnboardingHandler
import io.snaps.corecommon.R
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.startViewActionActivity
import io.snaps.corecommon.model.OnboardingType
import io.snaps.corecommon.strings.DEFAULT_LOCALE
import io.snaps.corecommon.strings.StringKey
import io.snaps.corecommon.strings.SupportedLanguageKey
import io.snaps.corecommon.strings.toSupportedLanguageKey
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.BottomBarFeatureProvider
import io.snaps.corenavigation.base.navigate
import io.snaps.corenavigation.base.openUrl
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.LocalBottomNavigationHeight
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.bottomsheetdialog.ModalBottomSheetCurrentStateListener
import io.snaps.coreuicompose.uikit.bottomsheetdialog.SimpleBottomDialog
import io.snaps.coreuicompose.uikit.bottomsheetdialog.SimpleBottomDialogUI
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionL
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
import io.snaps.coreuitheme.compose.colors
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

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    val firstDestRoutes = items.map { it.startDestination.path() }

    BackHandler(currentDestination?.route in firstDestRoutes.drop(1)) {
        navController.switchTo(items.first().route.pattern)
    }

    viewModel.updateMenuRoute(currentBackStackEntry?.destination?.route)

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val onboardingState by viewModel.onboardingUiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmValueChange = { uiState.appUpdateInfo == null } // app update dialog cannot be hidden
    )
    fun showSheet() = coroutineScope.launch { sheetState.show() }
    fun hideSheet() = coroutineScope.launch { sheetState.hide() }

    ModalBottomSheetCurrentStateListener(sheetState = sheetState, drop = 1) { isHidden ->
        if (isHidden) {
            viewModel.onCheckForBannerRequest()
        }
    }

    viewModel.command.collectAsCommand {
        when (it) {
            BottomBarViewModel.Command.OpenNftPurchaseScreen -> navController.navigate(AppRoute.RankSelection)
            BottomBarViewModel.Command.ShowBottomDialog -> showSheet()
            BottomBarViewModel.Command.HideBottomDialog -> hideSheet()
            is BottomBarViewModel.Command.OpenUrlScreen -> context.openUrl(it.url)
        }
    }
    viewModel.onboardingCommand.collectAsCommand {
        when (it) {
            is OnboardingHandler.Command.OpenDialog -> showSheet()
            OnboardingHandler.Command.HideDialog -> hideSheet()
            OnboardingHandler.Command.CheckForBanner -> viewModel.onCheckForBannerRequest()
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            when {
                uiState.appUpdateInfo != null -> {
                    SimpleBottomDialog(
                        image = R.drawable.img_guy_glad.imageValue(),
                        title = StringKey.AppUpdateTitle.textValue(),
                        text = StringKey.AppUpdateMessage.textValue(),
                        buttonText = StringKey.AppUpdateAction.textValue(),
                        onClick = {
                            uiState.appUpdateInfo?.let {
                                context.startViewActionActivity(Uri.parse(it.link))
                            }
                        },
                    )
                }
                uiState.banner != null -> uiState.banner?.let {
                    Banner(banner = it, onClicked = viewModel::onBannerActionClicked)
                }
                else -> OnboardingDialog(
                    onboardingState = onboardingState,
                    onClicked = viewModel::onOnboardingDialogActionClicked,
                )
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val bottomBarScreens = listOf(
                AppRoute.MainBottomBar.MainTab1Start,
                AppRoute.MainBottomBar.MainTab2Start,
                AppRoute.MainBottomBar.MainTab3Start,
                AppRoute.MainBottomBar.MainTab4Start,
                AppRoute.MainBottomBar.MainTab5Start,
            )
            val currentRoute = currentDestination?.route?.substringBefore(delimiter = "?")
            val bottomBarDestination = bottomBarScreens.any { it.path() == currentRoute }
            val isBottomBarVisible = uiState.isBottomBarVisible && bottomBarDestination
            CompositionLocalProvider(
                LocalBottomNavigationHeight provides (if (isBottomBarVisible) 80.dp else 0.dp)
            ) {
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
                if (isBottomBarVisible) {
                    val containerColor = colors {
                        if (currentRoute == AppRoute.MainBottomBar.MainTab1Start.path()) black
                        else white
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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
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
    when (onboardingState.onboardingType) {
        OnboardingType.Rank -> SimpleBottomDialog(
            image = R.drawable.img_guys_surprised_eating.imageValue(),
            title = StringKey.OnboardingRankTitle.textValue(),
            text = StringKey.OnboardingRankMessage.textValue(),
            buttonText = StringKey.OnboardingRankAction.textValue(),
            onClick = {
                onClicked(onboardingState.onboardingType)
            },
        )
        OnboardingType.Popular -> SimpleBottomDialog(
            image = R.drawable.img_guys_surprised_eating.imageValue(),
            title = StringKey.OnboardingPopularTitle.textValue(),
            text = StringKey.OnboardingPopularMessage.textValue(),
            buttonText = StringKey.OnboardingPopularAction.textValue(),
            onClick = {
                onClicked(onboardingState.onboardingType)
            },
        )
        OnboardingType.Tasks -> SimpleBottomDialog(
            image = R.drawable.img_guys_surprised_eating.imageValue(),
            title = StringKey.OnboardingTasksTitle.textValue(),
            text = StringKey.OnboardingTasksMessage.textValue(),
            buttonText = StringKey.OnboardingTasksAction.textValue(),
            onClick = {
                onClicked(onboardingState.onboardingType)
            },
        )
        OnboardingType.Nft -> SimpleBottomDialog(
            image = R.drawable.img_guys_surprised_eating.imageValue(),
            title = StringKey.OnboardingNftTitle.textValue(),
            text = StringKey.OnboardingNftText.textValue(),
            buttonText = StringKey.OnboardingNftAction.textValue(),
            onClick = {
                onClicked(onboardingState.onboardingType)
            },
        )
        OnboardingType.Referral -> SimpleBottomDialog(
            image = R.drawable.img_guys_surprised_eating.imageValue(),
            title = StringKey.OnboardingReferralTitle.textValue(),
            text = StringKey.OnboardingReferralMessage.textValue(),
            buttonText = StringKey.OnboardingReferralAction.textValue(),
            onClick = {
                onClicked(onboardingState.onboardingType)
            },
        )
        OnboardingType.Wallet -> SimpleBottomDialog(
            image = R.drawable.img_guys_surprised_eating.imageValue(),
            title = StringKey.OnboardingWalletTitle.textValue(),
            text = StringKey.OnboardingWalletText.textValue(),
            buttonText = StringKey.OnboardingWalletAction.textValue(),
            onClick = {
                onClicked(onboardingState.onboardingType)
            },
        )
        OnboardingType.Rewards -> SimpleBottomDialog(
            image = R.drawable.img_guys_surprised_eating.imageValue(),
            title = StringKey.OnboardingRewardsTitle.textValue(),
            text = StringKey.OnboardingRewardsMessage.textValue(),
            buttonText = StringKey.OnboardingRewardsAction.textValue(),
            onClick = {
                onClicked(onboardingState.onboardingType)
            },
        )
        null -> Box(modifier = Modifier.size(1.dp))
    }
}

@Composable
private fun Banner(
    banner: BannerDto,
    onClicked: (BannerDto) -> Unit,
) {
    val language = DEFAULT_LOCALE.toSupportedLanguageKey()
    val title = when (language) {
        SupportedLanguageKey.En -> banner.title.en
        SupportedLanguageKey.Ru -> banner.title.ru
        SupportedLanguageKey.Es -> banner.title.es
        SupportedLanguageKey.Tr -> banner.title.tr
        SupportedLanguageKey.Ua -> banner.title.uk
    }
    val actionTitle = when (language) {
        SupportedLanguageKey.En -> banner.actionTitle.en
        SupportedLanguageKey.Ru -> banner.actionTitle.ru
        SupportedLanguageKey.Es -> banner.actionTitle.es
        SupportedLanguageKey.Tr -> banner.actionTitle.tr
        SupportedLanguageKey.Ua -> banner.actionTitle.uk
    }
    Box(
        modifier = Modifier
            .background(AppTheme.specificColorScheme.white)
            .inset(insetAllExcludeTop()),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = banner.image.imageValue().get { crossfade(true) },
                contentDescription = null,
                modifier = Modifier
                    .heightIn(max = 240.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop,
            )
            Text(
                text = title,
                color = AppTheme.specificColorScheme.textPrimary,
                style = AppTheme.specificTypography.headlineSmall,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            )
            SimpleButtonActionL(
                onClick = { onClicked(banner) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                SimpleButtonContent(text = actionTitle.textValue())
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