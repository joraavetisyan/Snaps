package io.snaps.featurepopular

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corecommon.model.Uuid
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.PopularFeatureProvider
import io.snaps.corenavigation.base.FeatureNavDirection
import io.snaps.corenavigation.base.Navigator
import io.snaps.corenavigation.base.composable
import io.snaps.corenavigation.base.navigate
import io.snaps.featurepopular.presentation.screen.PopularVideoFeedScreen
import io.snaps.featurepopular.presentation.screen.PopularVideosScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) :
    Navigator(navHostController) {

    fun toProfileScreen(
        userId: Uuid? = null,
    ) = navHostController navigate FeatureNavDirection(
        route = AppRoute.Profile,
        arg = AppRoute.Profile.Args(userId = userId),
    )

    fun toPopularVideoFeedScreen(query: String, position: Int) =
        navHostController navigate FeatureNavDirection(
            route = AppRoute.PopularVideoFeed,
            arg = AppRoute.PopularVideoFeed.Args(query = query, position = position),
        )
}

class PopularFeatureProviderImpl @Inject constructor() : PopularFeatureProvider {

    override fun NavGraphBuilder.popularGraph(controller: NavHostController) {
        composable(AppRoute.MainBottomBar.MainTab2Start) { PopularVideosScreen(controller) }
        composable(AppRoute.PopularVideoFeed) { PopularVideoFeedScreen(controller) }
    }
}