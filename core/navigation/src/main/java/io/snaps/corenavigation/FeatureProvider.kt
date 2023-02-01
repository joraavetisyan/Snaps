package io.snaps.corenavigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.snaps.corecommon.container.IconValue
import io.snaps.corecommon.strings.StringKey

interface MainFeatureProvider {

    fun NavGraphBuilder.mock1Graph(controller: NavHostController)

    fun NavGraphBuilder.mock2Graph(controller: NavHostController)

    fun NavGraphBuilder.mock3Graph(controller: NavHostController)

    fun NavGraphBuilder.mock4Graph(controller: NavHostController)

    fun NavGraphBuilder.mock5Graph(controller: NavHostController)
}

interface BottomBarFeatureProvider {

    fun NavGraphBuilder.bottomBarGraph(route: Route, items: List<ScreenItem>)

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