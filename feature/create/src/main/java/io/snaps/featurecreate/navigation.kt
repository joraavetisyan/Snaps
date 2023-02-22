package io.snaps.featurecreate

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.CreateFeatureProvider
import io.snaps.corenavigation.base.FeatureNavDirection
import io.snaps.corenavigation.base.Navigator
import io.snaps.corenavigation.base.composable
import io.snaps.corenavigation.base.navigate
import io.snaps.featurecreate.screen.CreateVideoScreen
import io.snaps.featurecreate.screen.PreviewScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) :
    Navigator(navHostController) {

    fun toPreviewScreen(uri: String) = navHostController navigate FeatureNavDirection(
        AppRoute.PreviewVideo,
        AppRoute.PreviewVideo.Args(uri),
    )
}

class CreateFeatureProviderImpl @Inject constructor() : CreateFeatureProvider {

    override fun NavGraphBuilder.createGraph(controller: NavHostController) {
        composable(AppRoute.CreateVideo) { CreateVideoScreen(controller) }
        composable(AppRoute.PreviewVideo) { PreviewScreen(controller) }
    }
}