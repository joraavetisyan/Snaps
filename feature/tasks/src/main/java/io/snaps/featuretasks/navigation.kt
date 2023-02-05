package io.snaps.featuretasks

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corenavigation.TasksFeatureProvider
import io.snaps.corenavigation.base.Navigator
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) : Navigator(navHostController)

class TasksFeatureProviderImpl @Inject constructor() : TasksFeatureProvider {

    override fun NavGraphBuilder.tasksGraph(controller: NavHostController) {
    }
}