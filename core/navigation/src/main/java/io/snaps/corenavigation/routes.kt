package io.snaps.corenavigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import io.snaps.corecommon.model.SubsType
import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.Serializable

const val DefaultArgKey = "arg"

sealed class Route(protected val value: String) {

    open val pattern: String = value
    open val arguments: List<NamedNavArgument> = emptyList()

    open fun path(vararg args: Any): String = value
}

sealed class RouteWithArg(value: String) : Route(value) {

    override val pattern = "$value?$DefaultArgKey={$DefaultArgKey}"
    override val arguments = listOf(navArgument(DefaultArgKey) { type = NavType.StringType })

    override fun path(vararg args: Any): String = "$value?$DefaultArgKey=${args.first()}"
}

sealed class Deeplink(protected val value: String) {

    open val pattern: String = value

    open fun path(vararg args: Any): String = value
}

object AppRoute {

    object MainBottomBar : Route("MainBottomBar") {

        object MainTab1 : Route("MainTab1")
        object MainTab1Start : Route("MainTab1Start")

        object MainTab2 : Route("MainTab2")
        object MainTab2Start : Route("MainTab2Start")

        object MainTab3 : Route("MainTab3")
        object MainTab3Start : Route("MainTab3Start")

        object MainTab4 : Route("MainTab4")
        object MainTab4Start : Route("MainTab4Start")

        object MainTab5 : Route("MainTab5")
        object MainTab5Start : Route("MainTab5Start")
    }

    object Registration : Route("Registration")

    object WalletConnect : Route("WalletConnect")

    object WalletCreate : Route("WalletCreate")

    object WalletImport : Route("WalletImport")

    object WalletConnected : Route("WalletConnected")

    object Mnemonics : Route("Mnemonics")

    object MnemonicsVerification : RouteWithArg("MnemonicsVerification") {

        @Serializable
        data class Args(
            val words: List<String>,
        )
    }

    object UserCreate : Route("UserCreate")

    object Profile : RouteWithArg("Profile") {

        @Serializable
        data class Args(
            val userId: Uuid? = null,
        )
    }

    object CreateVideo : Route("CreateVideo")

    object Wallet : Route("Wallet")

    object Withdraw : Route("Withdraw")

    object ReferralProgramScreen : Route("ReferralProgramScreen")

    object Settings : Route("Settings")

    object SocialNetworks : Route("SocialNetworks")

    object BackupWalletKey : Route("BackupWalletKey")

    object WalletSettings : Route("WalletSettings")

    object Subs : RouteWithArg("Subs") {

        @Serializable
        data class Args(
            val userId: Uuid? = null,
            val subsPage: SubsType = SubsType.Subscriptions,
            val nickname: String,
            val totalSubscriptions: String,
            val totalSubscribers: String,
        )
    }

    @Serializable
    data class TaskArgs(
        val id: Uuid,
    )

    object FindPointsTask : RouteWithArg("FindPointsTask") // need pass TaskArgs

    object LikeAndSubscribeTask : RouteWithArg("LikeAndSubscribeTask") // need pass TaskArgs

    object ShareTask : RouteWithArg("ShareTask") // need pass TaskArgs

    object WatchVideoTask : RouteWithArg("WatchVideoTask") // need pass TaskArgs

    object PopularVideoFeed : RouteWithArg("PopularVideoFeed") {

        @Serializable
        data class Args(
            val query: String,
            val position: Int,
        )
    }

    object UserVideoFeed : RouteWithArg("UserVideoFeed") {

        @Serializable
        data class Args(
            val userId: Uuid?,
            val position: Int,
        )
    }

    object RankSelection : Route("RankSelection")

    object BuyNft : Route("BuyNft")
}