package io.snaps.android.mainscreen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.CallSuper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.snaps.android.di.AppViewModelFactory
import io.snaps.corecommon.model.BuildInfo
import io.snaps.corenavigation.base.navigate
import io.snaps.coreui.tools.EmulatorChecker
import io.snaps.coreuicompose.tools.SystemBarsIconsColor
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.coreuicompose.uikit.status.MessageBannerUi
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
import javax.inject.Inject

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class AppActivity : FragmentActivity() {

    @Inject
    lateinit var navHostProvider: NavHostProvider

    @Inject
    lateinit var buildInfo: BuildInfo

    private var shouldKeepSplashScreen = true

    @SuppressLint("SourceLockedOrientationActivity")
    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen().setKeepOnScreenCondition { shouldKeepSplashScreen }

        val isDeviceEmulated = EmulatorChecker.isDeviceEmulated() && buildInfo.isRelease

        setContent {
            if (isDeviceEmulated) finish() else AppScreen()
            AppTheme(calculateWindowSizeClass(this)) {
                SystemBarsIconsColor()
                AppScreen()
            }
        }
    }

    @Composable
    private fun AppScreen() {
        val navController = rememberNavController()
        val viewModel = viewModel<AppViewModel>(
            factory = AppViewModelFactory.provide(LocalContext.current as Activity)
        )
        val currentFlowState = viewModel.currentFlowState.collectAsState()
        val stringHolder by viewModel.stringHolderState.collectAsState()
        val notification by viewModel.notificationsState.collectAsState()
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        CompositionLocalProvider(LocalStringHolder provides stringHolder) {
            when (val currentFlow = currentFlowState.value) {
                AppViewModel.StartFlow.Idle -> Unit
                is AppViewModel.StartFlow.RegistrationFlow -> {
                    SideEffect { shouldKeepSplashScreen = false }
                    navHostProvider.NonAuthorizedGraph(
                        navController = navController,
                        needsStartOnBoarding = currentFlow.needsStartOnBoarding,
                    )
                }

                is AppViewModel.StartFlow.AuthorizedFlow -> {
                    if (currentFlow.isError) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            MessageBannerState.defaultState(onClick = viewModel::onRetry)
                        }
                    } else {
                        SideEffect { shouldKeepSplashScreen = false }
                        navHostProvider.AuthorizedGraph(
                            navController = navController,
                            needsWalletConnect = currentFlow.needsWalletConnect,
                            needsWalletImport = currentFlow.needsWalletImport,
                            needsInitialization = currentFlow.needsInitialization,
                        )
                        LaunchedEffect(currentFlow.isReady) {
                            if (currentFlow.isReady) {
                                navController.navigate(currentFlow.deeplink)
                                viewModel.applyReferralCode()
                            }
                        }
                        // When firebase dynamic links support is added, use this instead of the custom intent handle
                        /*Firebase.dynamicLinks
                            .getDynamicLink(intent)
                            .addOnSuccessListener {
                                navController.navigate(AppDeeplink.parse(it?.link.toString()))
                            }*/
                    }
                }
            }
            viewModel.updateAppRoute(navBackStackEntry?.destination?.route)
            MessageBannerUi(notification)
        }
    }
}