package io.snaps.featurecollection

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.CollectionFeatureProvider
import io.snaps.corenavigation.base.Navigator
import io.snaps.corenavigation.base.composable
import io.snaps.corenavigation.base.navigate
import io.snaps.featurecollection.presentation.screen.RankSelectionScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) : Navigator(navHostController) {

    fun toRankSelectionScreen() = navHostController.navigate(AppRoute.RankSelection)
}

class CollectionFeatureProviderImpl @Inject constructor() : CollectionFeatureProvider {

    override fun NavGraphBuilder.collectionGraph(controller: NavHostController) {
        composable(AppRoute.RankSelection) { RankSelectionScreen(controller) }
    }
}