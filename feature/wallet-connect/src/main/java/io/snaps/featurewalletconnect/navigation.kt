package io.snaps.featurewalletconnect

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.WalletConnectFeatureProvider
import io.snaps.corenavigation.base.FeatureNavDirection
import io.snaps.corenavigation.base.Navigator
import io.snaps.corenavigation.base.composable
import io.snaps.corenavigation.base.navigate
import io.snaps.featurewalletconnect.presentation.screen.MnemonicListVerificationScreen
import io.snaps.featurewalletconnect.presentation.screen.MnemonicsScreen
import io.snaps.featurewalletconnect.presentation.screen.WalletConnectScreen
import io.snaps.featurewalletconnect.presentation.screen.WalletConnectedScreen
import io.snaps.featurewalletconnect.presentation.screen.WalletCreateScreen
import io.snaps.featurewalletconnect.presentation.screen.WalletImportScreen
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
}

class WalletConnectFeatureProviderImpl @Inject constructor() : WalletConnectFeatureProvider {

    override fun NavGraphBuilder.walletConnectGraph(controller: NavHostController) {
        composable(AppRoute.WalletConnect) { WalletConnectScreen(controller) }
        composable(AppRoute.WalletCreate) { WalletCreateScreen(controller) }
        composable(AppRoute.WalletImport) { WalletImportScreen(controller) }
        composable(AppRoute.Mnemonics) { MnemonicsScreen(controller) }
        composable(AppRoute.MnemonicsVerification) { MnemonicListVerificationScreen(controller) }
        composable(AppRoute.WalletConnected) { WalletConnectedScreen(controller) }
    }
}