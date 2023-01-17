package com.defince.appdemo

import android.app.Application
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.CallSuper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.defince.appdemo.catalog.BottomSheetScreen
import com.defince.appdemo.catalog.ButtonsScreen
import com.defince.appdemo.catalog.CellTilesScreen
import com.defince.appdemo.catalog.DemoScreen
import com.defince.appdemo.catalog.InformationTilesScreen
import com.defince.appdemo.catalog.InputsScreen
import com.defince.appdemo.catalog.MessageScreen
import com.defince.appdemo.catalog.ModalBottomSheetScreen
import com.defince.appdemo.catalog.NumPadScreen
import com.defince.appdemo.catalog.PaletteScreen
import com.defince.appdemo.catalog.TagListScreen
import com.defince.appdemo.catalog.TypographyScreen
import com.defince.coredata.coroutine.ApplicationCoroutineScopeHolder
import com.defince.coredata.database.ThemeMode
import com.defince.coredata.database.UserDataStorage
import com.defince.coreuicompose.tools.SystemBarsIconsColor
import com.defince.coreuitheme.compose.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@HiltAndroidApp
class DemoApp : Application(), ApplicationCoroutineScopeHolder {

    override val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class DemoActivity : FragmentActivity() {

    @Inject lateinit var userDataStorage: UserDataStorage

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            Content()
        }
    }

    @Composable
    private fun Content() {
        val mode = userDataStorage.themeModeFlow.collectAsState(initial = ThemeMode.System)
        val isDarkTheme = when (mode.value) {
            ThemeMode.System -> isSystemInDarkTheme()
            ThemeMode.Dark -> true
            ThemeMode.Light -> false
        }
        AppTheme(calculateWindowSizeClass(this), isDarkTheme = isDarkTheme) {
            val navController = rememberNavController()

            SystemBarsIconsColor(needWhiteAllIcons = isDarkTheme)
            NavHost(navController = navController, startDestination = DemoDestinations.Demo) {
                composable(DemoDestinations.Demo, deepLinks = listOf(navDeepLink { uriPattern = DeeplinkDemo })) { DemoScreen(navController) }
                composable(DemoDestinations.Settings) { SettingsScreen(navController) }
                composable(DemoDestinations.Typography) { TypographyScreen(navController) }
                composable(DemoDestinations.Buttons) { ButtonsScreen(navController) }
                composable(DemoDestinations.Inputs) { InputsScreen(navController) }
                composable(DemoDestinations.CellTiles) { CellTilesScreen(navController) }
                composable(DemoDestinations.InformationTiles) { InformationTilesScreen(navController) }
                composable(DemoDestinations.Messages) { MessageScreen(navController) }
                composable(DemoDestinations.NumPad) { NumPadScreen(navController) }
                composable(DemoDestinations.Palette) { PaletteScreen(navController) }
                composable(DemoDestinations.TagList) { TagListScreen(navController) }
                composable(DemoDestinations.BottomSheet) { BottomSheetScreen(navController) }
                composable(DemoDestinations.ModalBottomSheet) { ModalBottomSheetScreen(navController) }
            }
        }
    }
}

object DemoDestinations {
    const val Demo = "Demo"
    const val Settings = "Settings"
    const val Typography = "Typography"
    const val Buttons = "Buttons"
    const val Inputs = "Inputs"
    const val CellTiles = "CellTiles"
    const val InformationTiles = "InformationTiles"
    const val Messages = "Messages"
    const val NumPad = "NumPad"
    const val Palette = "Palette"
    const val TagList = "TagList"
    const val BottomSheet = "BottomSheet"
    const val ModalBottomSheet = "ModalBottomSheet"
}

const val DeeplinkDemo = "app://com.defince.appdemo/demo"