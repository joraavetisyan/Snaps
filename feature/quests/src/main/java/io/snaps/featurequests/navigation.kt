package io.snaps.featurequests

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corecommon.model.Uuid
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.TasksFeatureProvider
import io.snaps.corenavigation.base.FeatureNavDirection
import io.snaps.corenavigation.base.Navigator
import io.snaps.corenavigation.base.composable
import io.snaps.corenavigation.base.navigate
import io.snaps.featurequests.presentation.screen.ConnectInstagramScreen
import io.snaps.featurequests.presentation.screen.QuestDetailsScreen
import io.snaps.featurequests.presentation.screen.QuestsScreen
import io.snaps.featurequests.presentation.screen.ShareTemplateScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) :
    Navigator(navHostController) {

    fun toShareTemplateScreen() = navHostController.navigate(AppRoute.ShareTemplate)

    fun toQuestDetailsScreen(
        args: AppRoute.QuestDetails.Args,
    ) = navHostController navigate FeatureNavDirection(
        AppRoute.QuestDetails,
        args,
    )

    fun toMainVideoFeedScreen() = navHostController.navigate(AppRoute.MainBottomBar.MainTab1Start)

    fun toCreateVideoScreen() = navHostController.navigate(AppRoute.CreateVideo)

    fun toProfileScreen(
        userId: Uuid? = null,
    ) = navHostController navigate FeatureNavDirection(
        route = AppRoute.Profile,
        arg = AppRoute.Profile.Args(userId = userId),
    )

    fun toWalletScreen() = navHostController.navigate(AppRoute.Wallet)

    fun toConnectInstagramScreen() = navHostController.navigate(AppRoute.ConnectInstagram)

    fun toUserNftDetailsScreen(
        args: AppRoute.UserNftDetails.Args
    ) = navHostController navigate FeatureNavDirection(
        route = AppRoute.UserNftDetails,
        arg = args,
    )

    fun backToTasksScreen() = navHostController.popBackStack(
        route = AppRoute.QuestDetails.pattern,
        inclusive = true
    )
}

class TasksFeatureProviderImpl @Inject constructor() : TasksFeatureProvider {

    override fun NavGraphBuilder.tasksGraph(controller: NavHostController) {
        composable(AppRoute.MainBottomBar.MainTab3Start) { QuestsScreen(controller) }
        composable(AppRoute.ShareTemplate) { ShareTemplateScreen(controller) }
        composable(AppRoute.QuestDetails) { QuestDetailsScreen(controller) }
        composable(AppRoute.ConnectInstagram) { ConnectInstagramScreen(controller) }
    }
}