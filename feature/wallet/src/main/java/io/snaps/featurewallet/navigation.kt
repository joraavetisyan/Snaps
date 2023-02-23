package io.snaps.featurewallet

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corecommon.model.WalletModel
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.WalletFeatureProvider
import io.snaps.corenavigation.base.FeatureNavDirection
import io.snaps.corenavigation.base.Navigator
import io.snaps.corenavigation.base.composable
import io.snaps.corenavigation.base.navigate
import io.snaps.featurewallet.screen.WalletScreen
import io.snaps.featurewallet.screen.WithdrawScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) : Navigator(navHostController) {

    fun toWithdrawScreen(walletModel: WalletModel) = navHostController navigate FeatureNavDirection(
        route = AppRoute.Withdraw,
        arg = AppRoute.Withdraw.Args(wallet = walletModel),
    )

    fun toProfileScreen() = navHostController.navigate(AppRoute.Profile)
}

class WalletFeatureProviderImpl @Inject constructor() : WalletFeatureProvider {

    override fun NavGraphBuilder.walletGraph(controller: NavHostController) {
        composable(AppRoute.Wallet) { WalletScreen(controller) }
        composable(AppRoute.Withdraw) { WithdrawScreen(controller) }
    }
}