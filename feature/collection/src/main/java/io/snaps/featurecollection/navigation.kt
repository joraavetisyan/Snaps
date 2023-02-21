package io.snaps.featurecollection

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corecommon.model.Uuid
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.CollectionFeatureProvider
import io.snaps.corenavigation.base.FeatureNavDirection
import io.snaps.corenavigation.base.Navigator
import io.snaps.corenavigation.base.composable
import io.snaps.corenavigation.base.navigate
import io.snaps.featurecollection.presentation.screen.BuyNftScreen
import io.snaps.featurecollection.presentation.screen.MyCollectionScreen
import io.snaps.featurecollection.presentation.screen.RankSelectionScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) :
    Navigator(navHostController) {

    fun toRankSelectionScreen() = navHostController.navigate(AppRoute.RankSelection)

    fun toMainScreen() = navHostController.navigate(AppRoute.MainBottomBar)

    fun toBuyNftScreen() = navHostController.navigate(AppRoute.BuyNft)

    fun toProfileScreen(
        userId: Uuid? = null,
    ) = navHostController navigate FeatureNavDirection(
        route = AppRoute.Profile,
        arg = AppRoute.Profile.Args(userId),
    )
}

class CollectionFeatureProviderImpl @Inject constructor() : CollectionFeatureProvider {

    override fun NavGraphBuilder.collectionGraph(controller: NavHostController) {
        composable(AppRoute.RankSelection) { RankSelectionScreen(controller) }
        composable(AppRoute.MainBottomBar.MainTab4Start) { MyCollectionScreen(controller) }
        composable(AppRoute.BuyNft) { BuyNftScreen(controller) }
    }
}