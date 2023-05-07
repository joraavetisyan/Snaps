package io.snaps.corenavigation.base

import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import io.snaps.corenavigation.AppDeeplink
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.Deeplink
import io.snaps.corenavigation.Route
import io.snaps.corenavigation.RouteWithArg
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        AppDeeplink.Invite -> navigate(AppRoute.ReferralProgramScreen)
        null -> Unit
    }
}

fun NavOptionsBuilder.tryPopBackStack(navHostController: NavHostController) {
    navHostController.currentDestination?.route?.let { popUpTo(it) { inclusive = true } }
}

fun NavOptionsBuilder.tryPopBackStack(route: String) {
    popUpTo(route) { inclusive = true }
}

fun <T> NavController.popBackStackWithResult(result: T) {
    previousBackStackEntry?.savedStateHandle?.set(RESULT_KEY, result)
    popBackStack()
}

inline infix fun <reified ARG> NavController.navigate(direction: FeatureNavDirection<ARG>) =
    navigate(
        route = direction.route.path(direction.arg.toDefaultFormat()),
        builder = direction.optionsBuilder,
    )

fun <T> NavController.resultFlow(): Flow<T>? {
    return currentBackStackEntry?.savedStateHandle?.getLiveData<T>(RESULT_KEY)?.asFlow()?.onEach {
        currentBackStackEntry?.savedStateHandle?.remove<T>(RESULT_KEY)
    }
}

@OptIn(DelicateCoroutinesApi::class)
private fun <T> LiveData<T>.asFlow(): Flow<T> = callbackFlow {
    val observer = Observer<T> {
        trySend(it)
    }
    withContext(Dispatchers.Main.immediate) {
        observeForever(observer)
    }
    awaitClose {
        GlobalScope.launch(Dispatchers.Main.immediate) {
            removeObserver(observer)
        }
    }
}.conflate()