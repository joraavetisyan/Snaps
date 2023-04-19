package io.snaps.featureprofile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corecommon.model.Uuid
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.ProfileFeatureProvider
import io.snaps.corenavigation.base.FeatureNavDirection
import io.snaps.corenavigation.base.Navigator
import io.snaps.corenavigation.base.composable
import io.snaps.corenavigation.base.navigate
import io.snaps.featureprofile.presentation.screen.ProfileScreen
import io.snaps.featureprofile.presentation.screen.SubsScreen
import io.snaps.featureprofile.presentation.screen.UserLikedVideoFeedScreen
import io.snaps.featureprofile.presentation.screen.UserVideoFeedScreen
import io.snaps.featureprofile.presentation.screen.settings.BackupWalletKeyScreen
import io.snaps.featureprofile.presentation.screen.settings.ReferralProgramScreen
import io.snaps.featureprofile.presentation.screen.settings.SettingsScreen
import io.snaps.featureprofile.presentation.screen.settings.SocialNetworksScreen
import io.snaps.featureprofile.presentation.screen.settings.WalletSettingsScreen
import javax.inject.Inject

// todo rename module to referral

internal class ScreenNavigator(navHostController: NavHostController) :
    Navigator(navHostController) {

    fun toReferralProgramScreen() = navHostController.navigate(AppRoute.ReferralProgramScreen)

    fun toSettingsScreen() = navHostController.navigate(AppRoute.Settings)

    fun toSocialNetworksScreen() = navHostController.navigate(AppRoute.SocialNetworks)

    fun toBackupWalletKeyScreen() = navHostController.navigate(AppRoute.BackupWalletKey)

    fun toWalletSettingsScreen() = navHostController.navigate(AppRoute.WalletSettings)

    fun toCreateVideoScreen() = navHostController.navigate(AppRoute.CreateVideo)

    fun toSubsScreen(
        args: AppRoute.Subs.Args
    ) = navHostController navigate FeatureNavDirection(
        route = AppRoute.Subs,
        arg = args,
    )

    fun toProfileScreen(
        userId: Uuid? = null,
    ) = navHostController navigate FeatureNavDirection(
        route = AppRoute.Profile,
        arg = AppRoute.Profile.Args(userId = userId),
    )

    fun toWalletScreen() = navHostController.navigate(AppRoute.Wallet)

    fun toUserVideoFeedScreen(userId: Uuid?, position: Int) =
        navHostController navigate FeatureNavDirection(
            route = AppRoute.UserVideoFeed,
            arg = AppRoute.UserVideoFeed.Args(userId = userId, position = position),
        )

    fun toUserLikedVideoFeedScreen(position: Int) =
        navHostController navigate FeatureNavDirection(
            route = AppRoute.UserLikedVideoFeed,
            arg = AppRoute.UserLikedVideoFeed.Args(position = position),
        )
}

class ProfileFeatureProviderImpl @Inject constructor() : ProfileFeatureProvider {

    override fun NavGraphBuilder.profileGraph(controller: NavHostController) {
        composable(AppRoute.MainBottomBar.MainTab5Start) { ReferralProgramScreen(controller) }
        composable(AppRoute.Profile) { ProfileScreen(controller) }
        composable(AppRoute.ReferralProgramScreen) { ReferralProgramScreen(controller) }
        composable(AppRoute.Settings) { SettingsScreen(controller) }
        composable(AppRoute.SocialNetworks) { SocialNetworksScreen(controller) }
        composable(AppRoute.BackupWalletKey) { BackupWalletKeyScreen(controller) }
        composable(AppRoute.WalletSettings) { WalletSettingsScreen(controller) }
        composable(AppRoute.Profile) { ProfileScreen(controller) }
        composable(AppRoute.Subs) { SubsScreen(controller) }
        composable(AppRoute.UserVideoFeed) { UserVideoFeedScreen(controller) }
        composable(AppRoute.UserLikedVideoFeed) { UserLikedVideoFeedScreen(controller) }
    }
}