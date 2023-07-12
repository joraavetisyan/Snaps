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
import io.snaps.featureprofile.presentation.screen.LikedFeedScreen
import io.snaps.featureprofile.presentation.screen.NotificationsScreen
import io.snaps.featureprofile.presentation.screen.UserFeedScreen
import io.snaps.featureprofile.presentation.screen.settings.AboutProjectScreen
import io.snaps.featureprofile.presentation.screen.settings.BackupWalletKeyScreen
import io.snaps.featureprofile.presentation.screen.settings.EditNameScreen
import io.snaps.featureprofile.presentation.screen.settings.EditProfileScreen
import io.snaps.featureprofile.presentation.screen.settings.SettingsScreen
import io.snaps.featureprofile.presentation.screen.settings.SocialNetworksScreen
import io.snaps.featureprofile.presentation.screen.settings.WalletSettingsScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) : Navigator(navHostController) {

    fun toReferralProgramScreen() = navHostController.navigate(AppRoute.ReferralProgram)

    fun toSettingsScreen() = navHostController.navigate(AppRoute.Settings)

    fun toSocialNetworksScreen() = navHostController.navigate(AppRoute.SocialNetworks)

    fun toBackupWalletKeyScreen() = navHostController.navigate(AppRoute.BackupWalletKey)

    fun toWalletSettingsScreen() = navHostController.navigate(AppRoute.WalletSettings)

    fun toWalletScreen() = navHostController.navigate(AppRoute.Wallet)

    fun toCreateVideoScreen() = navHostController.navigate(AppRoute.CreateVideo)

    fun toEditProfileScreen() = navHostController.navigate(AppRoute.EditProfile)

    fun toEditNameScreen() = navHostController.navigate(AppRoute.EditName)

    fun toAboutProjectScreen() = navHostController.navigate(AppRoute.AboutProject)

    fun toSubsScreen(
        args: AppRoute.Subs.Args,
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

    fun toUserFeedScreen(userId: Uuid?, position: Int) = navHostController navigate FeatureNavDirection(
        route = AppRoute.UserFeed,
        arg = AppRoute.UserFeed.Args(userId = userId, position = position),
    )

    fun toLikedFeedScreen(userId: Uuid?, position: Int) = navHostController navigate FeatureNavDirection(
        route = AppRoute.LikedFeed,
        arg = AppRoute.LikedFeed.Args(userId = userId, position = position),
    )

    fun toMainVideoFeedScreen() = navHostController.navigate(AppRoute.MainBottomBar.MainTab1Start)

    fun toNotificationsScreen() = navHostController.navigate(AppRoute.Notifications)
}

class ProfileFeatureProviderImpl @Inject constructor() : ProfileFeatureProvider {

    override fun NavGraphBuilder.profileGraph(controller: NavHostController) {
        composable(AppRoute.Profile) { ProfileScreen(controller) }
        composable(AppRoute.Settings) { SettingsScreen(controller) }
        composable(AppRoute.SocialNetworks) { SocialNetworksScreen(controller) }
        composable(AppRoute.BackupWalletKey) { BackupWalletKeyScreen(controller) }
        composable(AppRoute.WalletSettings) { WalletSettingsScreen(controller) }
        composable(AppRoute.Subs) { SubsScreen(controller) }
        composable(AppRoute.UserFeed) { UserFeedScreen(controller) }
        composable(AppRoute.LikedFeed) { LikedFeedScreen(controller) }
        composable(AppRoute.EditProfile) { EditProfileScreen(controller) }
        composable(AppRoute.EditName) { EditNameScreen(controller) }
        composable(AppRoute.AboutProject) { AboutProjectScreen(controller) }
        composable(AppRoute.Notifications) { NotificationsScreen(controller) }
    }
}