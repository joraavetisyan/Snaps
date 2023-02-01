package com.defince.featureregistration.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.defince.corenavigation.AppRoute
import com.defince.corenavigation.RegistrationFeatureProvider
import com.defince.corenavigation.base.Navigator
import com.defince.corenavigation.base.composable
import com.defince.corenavigation.base.navigate
import com.defince.featureregistration.presentation.screen.ConnectWalletScreen
import com.defince.featureregistration.presentation.screen.RegistrationScreen
import com.defince.featureregistration.presentation.screen.WalletImportScreen
import com.defince.featureregistration.presentation.screen.createwallet.CreateWalletScreen
import com.defince.featureregistration.presentation.screen.createwallet.CreatedWalletScreen
import com.defince.featureregistration.presentation.screen.createwallet.PhraseListScreen
import com.defince.featureregistration.presentation.screen.createwallet.VerificationScreen
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