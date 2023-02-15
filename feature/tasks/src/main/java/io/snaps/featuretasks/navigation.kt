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
import io.snaps.featuretasks.presentation.screen.FindPointsTaskScreen
import io.snaps.featuretasks.presentation.screen.LikeAndSubscribeTaskScreen
import io.snaps.featuretasks.presentation.screen.ShareTaskScreen
import io.snaps.featuretasks.presentation.screen.WatchVideoTaskScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) : Navigator(navHostController) {

    fun toShareTaskScreen(
        id: Uuid,
    ) = navHostController navigate FeatureNavDirection(
        AppRoute.ShareTask,
        AppRoute.FindPointsTask.Args(id),
    )

    fun toLikeAndSubscribeTaskScreen(
        id: Uuid,
    ) = navHostController navigate FeatureNavDirection(
        AppRoute.LikeAndSubscribeTask,
        AppRoute.FindPointsTask.Args(id),
    )

    fun toFindPointsTaskScreen(
        id: Uuid,
    ) = navHostController navigate FeatureNavDirection(
        AppRoute.FindPointsTask,
        AppRoute.FindPointsTask.Args(id),
    )

    fun toWatchVideoTaskScreen(
        id: Uuid,
    ) = navHostController navigate FeatureNavDirection(
        AppRoute.WatchVideoTask,
        AppRoute.FindPointsTask.Args(id),
    )
}

class TasksFeatureProviderImpl @Inject constructor() : TasksFeatureProvider {

    override fun NavGraphBuilder.tasksGraph(controller: NavHostController) {
        composable(AppRoute.ShareTask) { ShareTaskScreen(controller) }
        composable(AppRoute.LikeAndSubscribeTask) { LikeAndSubscribeTaskScreen(controller) }
        composable(AppRoute.FindPointsTask) { FindPointsTaskScreen(controller) }
        composable(AppRoute.WatchVideoTask) { WatchVideoTaskScreen(controller) }
    }
}