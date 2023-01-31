package com.defince.w2e.mainscreen

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.CallSuper
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.defince.corenavigation.BottomBarFeatureProvider
import com.defince.corenavigation.MainFeatureProvider
import com.defince.coreuicompose.tools.SystemBarsIconsColor
import com.defince.coreuicompose.uikit.status.MessageBannerUi
import com.defince.coreuitheme.compose.AppTheme
import com.defince.coreuitheme.compose.LocalStringHolder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class AppActivity : FragmentActivity() {

    @Inject
    lateinit var bottomBarFeatureProvider: BottomBarFeatureProvider

    @Inject
    lateinit var navHostProvider: NavHostProvider

    @Inject
    lateinit var mainFeatureProvider: MainFeatureProvider

    private var shouldKeepSplashScreen = true

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen().setKeepOnScreenCondition { shouldKeepSplashScreen }
        setContent {
            AppTheme(calculateWindowSizeClass(this)) {
                SideEffect { shouldKeepSplashScreen = false }
                SystemBarsIconsColor()
                AppScreen()
            }
        }
    }

    @Composable
    private fun AppScreen() {
        val navController = rememberNavController()
        val viewModel = viewModel<AppViewModel>()
        val currentFlowState = viewModel.currentFlowState.collectAsState()
        val stringHolder by viewModel.stringHolderState.collectAsState()
        val notification by viewModel.notificationsState.collectAsState()
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        CompositionLocalProvider(LocalStringHolder provides stringHolder) {
            when (val currentFlow = currentFlowState.value) {
                is AppViewModel.StartFlow.RegistrationFlow -> navHostProvider.RegistrationNavHost(
                    navController = navController,
                    needOnBoarding = currentFlow.needStartOnBoarding,
                )
                is AppViewModel.StartFlow.AuthorizedFlow -> navHostProvider.AuthorizedGraph(
                    navController = navController,
                )
            }
            viewModel.updateAppRoute(navBackStackEntry?.destination?.route)
            MessageBannerUi(notification)
        }
    }
}