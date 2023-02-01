package io.snaps.featureregistration.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.RegistrationFeatureProvider
import io.snaps.corenavigation.base.Navigator
import io.snaps.corenavigation.base.composable
import io.snaps.corenavigation.base.navigate
import io.snaps.featureregistration.presentation.screen.ConnectWalletScreen
import io.snaps.featureregistration.presentation.screen.RegistrationScreen
import io.snaps.featureregistration.presentation.screen.WalletImportScreen
import io.snaps.featureregistration.presentation.screen.createwallet.CreateWalletScreen
import io.snaps.featureregistration.presentation.screen.createwallet.CreatedWalletScreen
import io.snaps.featureregistration.presentation.screen.createwallet.PhraseListScreen
import io.snaps.featureregistration.presentation.screen.createwallet.VerificationScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) : Navigator(navHostController) {

    fun toWalletImportScreen() = navHostController.navigate(AppRoute.WalletImport)

    fun toConnectWalletScreen() = navHostController.navigate(AppRoute.ConnectWallet)

    fun toCreateWalletScreen() = navHostController.navigate(AppRoute.CreateWallet)

    fun toPhraseListScreen() = navHostController.navigate(AppRoute.PhraseList)

    fun toVerificationScreen() = navHostController.navigate(AppRoute.Verification)

    fun toCreatedWalletScreen() = navHostController.navigate(AppRoute.Verification)
}

class RegistrationFeatureProviderImpl @Inject constructor() : RegistrationFeatureProvider {
    override fun NavGraphBuilder.registrationGraph(controller: NavHostController) {
        composable(AppRoute.Registration) { RegistrationScreen(controller) }
        composable(AppRoute.WalletImport) { WalletImportScreen(controller) }
        composable(AppRoute.ConnectWallet) { ConnectWalletScreen(controller) }
        composable(AppRoute.CreateWallet) { CreateWalletScreen(controller) }
        composable(AppRoute.PhraseList) { PhraseListScreen(controller) }
        composable(AppRoute.Verification) { VerificationScreen(controller) }
        composable(AppRoute.CreatedWallet) { CreatedWalletScreen(controller) }
    }
}