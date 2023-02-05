package io.snaps.featurefeed

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corenavigation.FeedFeatureProvider
import io.snaps.corenavigation.base.Navigator
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) : Navigator(navHostController)

class FeedFeatureProviderImpl @Inject constructor() : FeedFeatureProvider {

    override fun NavGraphBuilder.feedGraph(controller: NavHostController) {
    }
}