package io.snaps.featureprofile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.ProfileFeatureProvider
import io.snaps.corenavigation.base.FeatureNavDirection
import io.snaps.corenavigation.base.Navigator
import io.snaps.corenavigation.base.composable
import io.snaps.corenavigation.base.navigate
import io.snaps.featureprofile.presentation.screen.ProfileScreen
import io.snaps.featureprofile.presentation.screen.SubsScreen
import io.snaps.featureprofile.presentation.screen.settings.BackupWalletKeyScreen
import io.snaps.featureprofile.presentation.screen.settings.ReferralProgramScreen
import io.snaps.featureprofile.presentation.screen.settings.SettingsScreen
import io.snaps.featureprofile.presentation.screen.settings.SocialNetworksScreen
import io.snaps.featureprofile.presentation.screen.settings.WalletSettingsScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) :
    Navigator(navHostController) {

    fun toReferralProgramScreen() = navHostController.navigate(AppRoute.ReferralProgramScreen)

    fun toSettingsScreen() = navHostController.navigate(AppRoute.Settings)

    fun toSocialNetworksScreen() = navHostController.navigate(AppRoute.SocialNetworks)

    fun toBackupWalletKeyScreen() = navHostController.navigate(AppRoute.BackupWalletKey)

    fun toWalletSettingsScreen() = navHostController.navigate(AppRoute.WalletSettings)

    fun toSubsScreen(
        args: AppRoute.Subs.Args
    ) = navHostController navigate FeatureNavDirection(
        AppRoute.Subs,
        args,
    )

    fun toProfileScreen(
        args: AppRoute.Profile.Args
    ) = navHostController navigate FeatureNavDirection(
        AppRoute.Profile,
        args,
    )
}

class ProfileFeatureProviderImpl @Inject constructor() : ProfileFeatureProvider {

    override fun NavGraphBuilder.profileGraph(controller: NavHostController) {
        composable(AppRoute.Profile) { ProfileScreen(controller) }
        composable(AppRoute.ReferralProgramScreen) { ReferralProgramScreen(controller) }
        composable(AppRoute.Settings) { SettingsScreen(controller) }
        composable(AppRoute.SocialNetworks) { SocialNetworksScreen(controller) }
        composable(AppRoute.BackupWalletKey) { BackupWalletKeyScreen(controller) }
        composable(AppRoute.WalletSettings) { WalletSettingsScreen(controller) }
        composable(AppRoute.Profile) { ProfileScreen(controller) }
        composable(AppRoute.Subs) { SubsScreen(controller) }
    }
}