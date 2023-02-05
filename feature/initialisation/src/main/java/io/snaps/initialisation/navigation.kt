package io.snaps.initialisation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.InitialisationFeatureProvider
import io.snaps.corenavigation.base.Navigator
import io.snaps.corenavigation.base.composable
import io.snaps.corenavigation.base.navigate
import io.snaps.initialisation.screen.WalletImportScreen
import io.snaps.initialisation.screen.ConnectWalletScreen
import io.snaps.initialisation.screen.CreateUserScreen
import io.snaps.initialisation.screen.createwallet.CreateWalletScreen
import io.snaps.initialisation.screen.createwallet.CreatedWalletScreen
import io.snaps.initialisation.screen.createwallet.PhraseListScreen
import io.snaps.initialisation.screen.createwallet.VerificationScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) : Navigator(navHostController) {

    fun toWalletImportScreen() = navHostController.navigate(AppRoute.WalletImport)

    fun toCreateWalletScreen() = navHostController.navigate(AppRoute.CreateWallet)

    fun toPhraseListScreen() = navHostController.navigate(AppRoute.PhraseList)

    fun toVerificationScreen() = navHostController.navigate(AppRoute.Verification)

    fun toCreatedWalletScreen() = navHostController.navigate(AppRoute.Verification)
}

class InitialisationFeatureProviderImpl @Inject constructor() : InitialisationFeatureProvider {

    override fun NavGraphBuilder.initialisationGraph(controller: NavHostController) {
        composable(AppRoute.CreateUser) { CreateUserScreen(controller) }
        composable(AppRoute.WalletImport) { WalletImportScreen(controller) }
        composable(AppRoute.ConnectWallet) { ConnectWalletScreen(controller) }
        composable(AppRoute.CreateWallet) { CreateWalletScreen(controller) }
        composable(AppRoute.PhraseList) { PhraseListScreen(controller) }
        composable(AppRoute.Verification) { VerificationScreen(controller) }
        composable(AppRoute.CreatedWallet) { CreatedWalletScreen(controller) }
    }
}