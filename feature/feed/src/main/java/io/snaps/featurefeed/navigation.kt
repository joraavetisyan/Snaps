package io.snaps.featurefeed

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.FeedFeatureProvider
import io.snaps.corenavigation.base.Navigator
import io.snaps.corenavigation.base.navigate
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) : Navigator(navHostController) {

    fun toProfileScreen() = navHostController.navigate(AppRoute.Profile)

    fun toWalletScreen() = navHostController.navigate(AppRoute.Wallet)
}

class FeedFeatureProviderImpl @Inject constructor() : FeedFeatureProvider {

    override fun NavGraphBuilder.feedGraph(controller: NavHostController) {
    }
}