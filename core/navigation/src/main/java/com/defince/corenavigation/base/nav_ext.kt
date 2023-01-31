package com.defince.corenavigation.base

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.defince.corenavigation.Deeplink
import com.defince.corenavigation.Route
import com.defince.corenavigation.RouteWithArg
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val RESULT_KEY = "result"
const val ARG_KEY = "arg"
const val ROUTE_ARGS_SEPARATOR = '?'

inline fun <reified ARG> ARG.toQuery() = ARG_KEY + "=" + Json.encodeToString(this)
inline fun <reified ARG> SavedStateHandle.getArg(): ARG? =
    this.get<String>(ARG_KEY)?.let { Json.decodeFromString(it) }

inline fun <reified ARG> SavedStateHandle.requireArgs(): ARG = requireNotNull(getArg())

fun createRoute(route: Route) = route.path()
fun createRouteWithArg(route: RouteWithArg) = "${route.path()}?$ARG_KEY={$ARG_KEY}"
inline fun <reified ARG> createRouteWithArg(route: RouteWithArg, arg: ARG) =
    "${route.path()}?${arg.toQuery()}"


fun NavGraphBuilder.composable(
    route: Route,
    vararg deeplink: Deeplink,
    content: @Composable (NavBackStackEntry) -> Unit
) = composable(
    route = route.pattern,
    arguments = route.arguments,
    deepLinks = deeplink.toList().map { data -> navDeepLink { uriPattern = data.pattern } },
    content = content,
)

fun NavController.navigate(route: Route, builder: NavOptionsBuilder.() -> Unit = {}) = navigate(
    route = route.path(),
    builder = builder,
)

inline infix fun <reified ARG> NavController.navigate(direction: FeatureNavDirection<ARG>) =
    navigate(
        route = direction.route.path(direction.arg.toDefaultFormat()),
        builder = direction.optionsBuilder,
    )