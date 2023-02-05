package io.snaps.featureprofile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.ProfileFeatureProvider
import io.snaps.corenavigation.base.Navigator
import io.snaps.corenavigation.base.composable
import io.snaps.corenavigation.base.navigate
import io.snaps.featureprofile.screen.settings.BackupWalletKeyScreen
import io.snaps.featureprofile.screen.settings.ReferralProgramScreen
import io.snaps.featureprofile.screen.settings.SettingsScreen
import io.snaps.featureprofile.screen.settings.SocialNetworksScreen
import io.snaps.featureprofile.screen.settings.WalletSettingsScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) :
    Navigator(navHostController) {

    fun toReferralProgramScreen() = navHostController.navigate(AppRoute.ReferralProgramScreen)

    fun toSettingsScreen() = navHostController.navigate(AppRoute.Settings)

    fun toSocialNetworksScreen() = navHostController.navigate(AppRoute.SocialNetworks)

    fun toBackupWalletKeyScreen() = navHostController.navigate(AppRoute.BackupWalletKey)

    fun toWalletSettingsScreen() = navHostController.navigate(AppRoute.WalletSettings)
}

class ProfileFeatureProviderImpl @Inject constructor() : ProfileFeatureProvider {

    override fun NavGraphBuilder.profileGraph(controller: NavHostController) {

        composable(AppRoute.ReferralProgramScreen) { ReferralProgramScreen(controller) }
        composable(AppRoute.Settings) { SettingsScreen(controller) }
        composable(AppRoute.SocialNetworks) { SocialNetworksScreen(controller) }
        composable(AppRoute.BackupWalletKey) { BackupWalletKeyScreen(controller) }
        composable(AppRoute.WalletSettings) { WalletSettingsScreen(controller) }
    }
}