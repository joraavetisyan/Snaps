package io.snaps.featuretasks

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corecommon.model.Uuid
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.TasksFeatureProvider
import io.snaps.corenavigation.base.FeatureNavDirection
import io.snaps.corenavigation.base.Navigator
import io.snaps.corenavigation.base.composable
import io.snaps.corenavigation.base.navigate
import io.snaps.featuretasks.presentation.screen.FindPointsScreen
import io.snaps.featuretasks.presentation.screen.ShareTemplateScreen
import io.snaps.featuretasks.presentation.screen.TaskDetailsScreen
import io.snaps.featuretasks.presentation.screen.TasksScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) :
    Navigator(navHostController) {

    fun toShareTemplateScreen() = navHostController.navigate(AppRoute.ShareTemplate)

    fun toFindPointsScreen() = navHostController.navigate(AppRoute.FindPoints)

    fun toTaskDetailsScreen(
        args: AppRoute.TaskDetails.Args,
    ) = navHostController navigate FeatureNavDirection(
        AppRoute.TaskDetails,
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
}

class TasksFeatureProviderImpl @Inject constructor() : TasksFeatureProvider {

    override fun NavGraphBuilder.tasksGraph(controller: NavHostController) {
        composable(AppRoute.MainBottomBar.MainTab3Start) { TasksScreen(controller) }
        composable(AppRoute.ShareTemplate) { ShareTemplateScreen(controller) }
        composable(AppRoute.TaskDetails) { TaskDetailsScreen(controller) }
        composable(AppRoute.FindPoints) { FindPointsScreen(controller) }
    }
}