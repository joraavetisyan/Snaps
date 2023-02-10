package io.snaps.featureregistration.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.RegistrationFeatureProvider
import io.snaps.corenavigation.base.Navigator
import io.snaps.corenavigation.base.composable
import io.snaps.corenavigation.base.navigate
import io.snaps.featureregistration.presentation.screen.RegistrationScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) : Navigator(navHostController) {

    fun toConnectWalletScreen() = navHostController.navigate(AppRoute.WalletConnect)
}

class RegistrationFeatureProviderImpl @Inject constructor() : RegistrationFeatureProvider {

    override fun NavGraphBuilder.registrationGraph(controller: NavHostController) {
        composable(AppRoute.Registration) { RegistrationScreen(controller) }
    }
}