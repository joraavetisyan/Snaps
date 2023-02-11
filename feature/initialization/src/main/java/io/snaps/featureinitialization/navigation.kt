package io.snaps.featureinitialization

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.InitializationFeatureProvider
import io.snaps.corenavigation.base.FeatureNavDirection
import io.snaps.corenavigation.base.Navigator
import io.snaps.corenavigation.base.composable
import io.snaps.corenavigation.base.navigate
import io.snaps.featureinitialization.screen.CreateUserScreen
import io.snaps.featureinitialization.screen.MnemonicListVerificationScreen
import io.snaps.featureinitialization.screen.MnemonicsScreen
import io.snaps.featureinitialization.screen.WalletConnectScreen
import io.snaps.featureinitialization.screen.WalletConnectedScreen
import io.snaps.featureinitialization.screen.WalletCreateScreen
import io.snaps.featureinitialization.screen.WalletImportScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) :
    Navigator(navHostController) {

    fun toWalletImportScreen() = navHostController.navigate(AppRoute.WalletImport)

    fun toWalletCreateScreen() = navHostController.navigate(AppRoute.WalletCreate)

    fun toMnemonicsScreen() = navHostController.navigate(AppRoute.Mnemonics)

    fun toVerificationScreen(words: List<String>) = navHostController navigate FeatureNavDirection(
        route = AppRoute.MnemonicsVerification,
        arg = AppRoute.MnemonicsVerification.Args(words),
    )

    fun toWalletConnectedScreen() = navHostController.navigate(AppRoute.WalletConnected)

    fun toCreateUserScreen() = navHostController.navigate(AppRoute.CreateUser)
}

class InitializationFeatureProviderImpl @Inject constructor() : InitializationFeatureProvider {

    override fun NavGraphBuilder.initializationGraph(controller: NavHostController) {
        composable(AppRoute.CreateUser) { CreateUserScreen(controller) }
        composable(AppRoute.WalletImport) { WalletImportScreen(controller) }
        composable(AppRoute.WalletConnect) { WalletConnectScreen(controller) }
        composable(AppRoute.WalletCreate) { WalletCreateScreen(controller) }
        composable(AppRoute.Mnemonics) { MnemonicsScreen(controller) }
        composable(AppRoute.MnemonicsVerification) { MnemonicListVerificationScreen(controller) }
        composable(AppRoute.WalletConnected) { WalletConnectedScreen(controller) }
    }
}