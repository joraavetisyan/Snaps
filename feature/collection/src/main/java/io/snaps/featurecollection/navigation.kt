package io.snaps.featurecollection

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.Uuid
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.CollectionFeatureProvider
import io.snaps.corenavigation.base.FeatureNavDirection
import io.snaps.corenavigation.base.Navigator
import io.snaps.corenavigation.base.composable
import io.snaps.corenavigation.base.navigate
import io.snaps.featurecollection.presentation.screen.MyCollectionScreen
import io.snaps.featurecollection.presentation.screen.PurchaseScreen
import io.snaps.featurecollection.presentation.screen.RankSelectionScreen
import io.snaps.featurecollection.presentation.screen.UserNftDetailsScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) :
    Navigator(navHostController) {

    fun toRankSelectionScreen() = navHostController.navigate(AppRoute.RankSelection)

    fun toPurchaseScreen(
        args: AppRoute.Purchase.Args
    ) = navHostController navigate FeatureNavDirection(
        route = AppRoute.Purchase,
        arg = args,
    )

    fun toProfileScreen(
        userId: Uuid? = null,
    ) = navHostController navigate FeatureNavDirection(
        route = AppRoute.Profile,
        arg = AppRoute.Profile.Args(userId),
    )

    fun toWalletScreen() = navHostController.navigate(AppRoute.Wallet)

    fun toUserNftDetailsScreen(
        args: AppRoute.UserNftDetails.Args
    ) = navHostController navigate FeatureNavDirection(
        route = AppRoute.UserNftDetails,
        arg = args,
    )

    fun backToMyCollectionScreen() = navHostController.popBackStack(
        route = AppRoute.RankSelection.path(),
        inclusive = true
    )

    fun toWebView(
        url: FullUrl,
    ) = navHostController navigate FeatureNavDirection(
        route = AppRoute.WebView,
        arg = AppRoute.WebView.Args(url),
    )
}

class CollectionFeatureProviderImpl @Inject constructor() : CollectionFeatureProvider {

    override fun NavGraphBuilder.collectionGraph(controller: NavHostController) {
        composable(AppRoute.RankSelection) { RankSelectionScreen(controller) }
        composable(AppRoute.MainBottomBar.MainTab4Start) { MyCollectionScreen(controller) }
        composable(AppRoute.Purchase) { PurchaseScreen(controller) }
        composable(AppRoute.UserNftDetails) { UserNftDetailsScreen(controller) }
    }
}