package io.snaps.corenavigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

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

    object WalletImport : Route("WalletImport")

    object ConnectWallet : Route("ConnectWallet")

    object CreateWallet : Route("CreateWallet")

    object CreatedWallet : Route("CreatedWallet")

    object PhraseList : Route("PhraseList")

    object Verification : Route("Verification")

    object CreateUser : Route("CreateUser")

    object Wallet : Route("Wallet")

    object Withdraw : Route("Withdraw")

    object ReferralProgramScreen : Route("ReferralProgramScreen")

    object Settings : Route("Settings")

    object SocialNetworks : Route("SocialNetworks")

    object BackupWalletKey : Route("BackupWalletKey")

    object WalletSettings : Route("WalletSettings")
}