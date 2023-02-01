package com.defince.corenavigation

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

        object MainTab1 : Route("mainTab1")
        object Mock1 : Route("Mock1")

        object MainTab2 : Route("mainTab2")
        object Mock2 : Route("Mock2")

        object MainTab3 : Route("mainTab3")
        object Mock3 : Route("Mock3")

        object MainTab4 : Route("mainTab4")
        object Mock4 : Route("Mock4")

        object MainTab5 : Route("mainTab5")
        object Mock5 : Route("Mock5")
    }

    object Registration : Route("Registration")

    object WalletImport : Route("WalletImport")

    object ConnectWallet : Route("ConnectWallet")

    object CreateWallet : Route("CreateWallet")

    object CreatedWallet : Route("CreatedWallet")

    object PhraseList : Route("PhraseList")

    object Verification : Route("Verification")
}