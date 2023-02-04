package io.snaps.featurewallet

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.WalletFeatureProvider
import io.snaps.corenavigation.base.Navigator
import io.snaps.corenavigation.base.composable
import io.snaps.corenavigation.base.navigate
import io.snaps.featurewallet.screen.WalletScreen
import io.snaps.featurewallet.screen.WithdrawScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) : Navigator(navHostController) {

    fun toWalletScreen() = navHostController.navigate(AppRoute.Wallet)

    fun toWithdrawScreen() = navHostController.navigate(AppRoute.Withdraw)
}

class WalletFeatureProviderImpl @Inject constructor() : WalletFeatureProvider {

    override fun NavGraphBuilder.walletGraph(controller: NavHostController) {
        composable(AppRoute.Wallet) { WalletScreen(controller) }
        composable(AppRoute.Withdraw) { WithdrawScreen(controller) }
    }
}