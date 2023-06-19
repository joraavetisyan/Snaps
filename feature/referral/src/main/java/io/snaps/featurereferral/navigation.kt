package io.snaps.featurereferral

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corecommon.model.Uuid
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.ReferralFeatureProvider
import io.snaps.corenavigation.base.FeatureNavDirection
import io.snaps.corenavigation.base.Navigator
import io.snaps.corenavigation.base.composable
import io.snaps.corenavigation.base.navigate
import io.snaps.featurereferral.presentation.screen.ReferralProgramScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) : Navigator(navHostController) {

    fun toProfileScreen(
        userId: Uuid? = null,
    ) = navHostController navigate FeatureNavDirection(
        route = AppRoute.Profile,
        arg = AppRoute.Profile.Args(userId = userId),
    )

    fun toWalletScreen() = navHostController.navigate(AppRoute.Wallet)
}

class ReferralFeatureProviderImpl @Inject constructor() : ReferralFeatureProvider {

    override fun NavGraphBuilder.referralGraph(controller: NavHostController) {
        composable(AppRoute.MainBottomBar.MainTab5Start) { ReferralProgramScreen(controller) }
        composable(AppRoute.ReferralProgram) { ReferralProgramScreen(controller) }
    }
}