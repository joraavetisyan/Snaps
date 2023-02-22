package io.snaps.corenavigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corecommon.container.IconValue
import io.snaps.corecommon.strings.StringKey

interface BottomBarFeatureProvider {

    fun NavGraphBuilder.bottomBarGraph(
        route: Route,
        items: List<ScreenItem>,
        builder: NavGraphBuilder.(NavHostController) -> Unit,
    )

    data class ScreenItem(
        val icon: IconValue,
        val labelKey: StringKey? = null,
        val route: Route,
        val startDestination: Route,
        val builder: NavGraphBuilder.(NavHostController) -> Unit,
    )
}

interface RegistrationFeatureProvider {

    fun NavGraphBuilder.registrationGraph(controller: NavHostController)
}

interface InitializationFeatureProvider {

    fun NavGraphBuilder.initializationGraph(controller: NavHostController)
}

interface WalletConnectFeatureProvider {

    fun NavGraphBuilder.walletConnectGraph(controller: NavHostController)
}

interface ProfileFeatureProvider {

    fun NavGraphBuilder.profileGraph(controller: NavHostController)
}

interface WalletFeatureProvider {

    fun NavGraphBuilder.walletGraph(controller: NavHostController)
}

interface CreateFeatureProvider {

    fun NavGraphBuilder.createGraph(controller: NavHostController)
}

interface TasksFeatureProvider {

    fun NavGraphBuilder.tasksGraph(controller: NavHostController)
}

interface CollectionFeatureProvider {

    fun NavGraphBuilder.collectionGraph(controller: NavHostController)
}

interface FeedFeatureProvider {

    fun NavGraphBuilder.feedGraph(controller: NavHostController)
}

interface PopularFeatureProvider {

    fun NavGraphBuilder.popularGraph(controller: NavHostController)
}