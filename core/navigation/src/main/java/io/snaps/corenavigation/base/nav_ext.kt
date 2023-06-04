package io.snaps.corenavigation.base

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import io.snaps.corecommon.ext.asFlow
import io.snaps.corenavigation.AppDeeplink
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.Deeplink
import io.snaps.corenavigation.Route
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val RESULT_KEY = "result"
const val ARG_KEY = "arg"
const val ROUTE_ARGS_SEPARATOR = '?'

inline fun <reified ARG> SavedStateHandle.getArg(): ARG? = get<String>(ARG_KEY)?.let { Json.decodeFromString(it) }
inline fun <reified ARG> SavedStateHandle.requireArgs(): ARG = requireNotNull(getArg())

fun createRoute(route: Route) = route.path()

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

fun NavController.navigate(deeplink: Deeplink?, builder: NavOptionsBuilder.() -> Unit = {}) {
    when (deeplink) {
        is AppDeeplink.Profile -> this navigate FeatureNavDirection(
            route = AppRoute.Profile,
            arg = AppRoute.Profile.Args(deeplink.id),
            optionsBuilder = builder,
        )
        is AppDeeplink.VideoClip -> this navigate FeatureNavDirection(
            route = AppRoute.SingleVideo,
            arg = AppRoute.SingleVideo.Args(deeplink.id),
            optionsBuilder = builder,
        )
        is AppDeeplink.Invite -> navigate(AppRoute.ReferralProgramScreen)
        null -> Unit
    }
}

fun NavOptionsBuilder.tryPopBackStack(navHostController: NavHostController) {
    navHostController.currentDestination?.route?.let { popUpTo(it) { inclusive = true } }
}

fun NavOptionsBuilder.tryPopBackStack(route: String) {
    popUpTo(route) { inclusive = true }
}

inline fun <reified T> NavController.popBackStackWithResult(
    result: T,
) {
    previousBackStackEntry?.savedStateHandle?.set(RESULT_KEY, Json.encodeToString(result))
    popBackStack()
}

inline fun <reified T> NavController.popBackStackWithResult(
    result: T,
    route: String,
) {
    getBackStackEntry(route).savedStateHandle[RESULT_KEY] = Json.encodeToString(result)
    popBackStack(route = route, inclusive = false, saveState = true)
}

inline fun <reified T> NavController.resultFlow(): Flow<T>? {
    return currentBackStackEntry?.savedStateHandle?.getLiveData<String>(RESULT_KEY)?.asFlow()?.onEach {
        currentBackStackEntry?.savedStateHandle?.remove<T>(RESULT_KEY)
    }?.map { Json.decodeFromString(it) }
}

inline infix fun <reified ARG> NavController.navigate(direction: FeatureNavDirection<ARG>) = navigate(
    route = direction.route.path(direction.arg.toDefaultFormat()),
    builder = direction.optionsBuilder,
)