package com.defince.corenavigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.defince.corecommon.container.IconValue
import com.defince.corecommon.strings.StringKey

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