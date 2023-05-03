package io.snaps.corenavigation

import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.QuestType
import io.snaps.corecommon.model.SubsType
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.model.WalletModel
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

    override fun path(vararg args: Any): String = "$value?$DefaultArgKey=${args.firstOrNull()}"
}

sealed class Deeplink(protected val value: String) {

    open val pattern: String = value

    open fun path(): String = value
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

    object Checking : Route("Checking")

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

    object PreviewVideo : RouteWithArg("PreviewVideo") {

        @Serializable
        data class Args(
            val uri: String,
        )
    }

    object UploadVideo : RouteWithArg("UploadVideo") {

        @Serializable
        data class Args(
            val uri: String,
        )
    }

    object Wallet : Route("Wallet")

    object Withdraw : RouteWithArg("Withdraw") {

        @Serializable
        data class Args(
            val wallet: WalletModel,
        )
    }

    object Exchange : RouteWithArg("Exchange") {

        @Serializable
        data class Args(
            val wallet: WalletModel,
        )
    }

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

    object FindPoints : Route("FindPoints")

    object ShareTemplate : Route("ShareTemplate")

    object TaskDetails : RouteWithArg("TaskDetails") {

        @Serializable
        data class Args(
            val type: QuestType,
            val energy: Int,
            val energyProgress: Int,
            val completed: Boolean,
        )
    }

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

    object UserLikedVideoFeed : RouteWithArg("UserLikedVideoFeed") {

        @Serializable
        data class Args(
            val position: Int,
        )
    }

    object RankSelection : Route("RankSelection")

    object Purchase : RouteWithArg("Purchase") {

        @Serializable
        data class Args(
            val type: NftType,
            val image: FullUrl, // todo why FullUrl?
            val dailyReward: Int,
            val dailyUnlock: Double,
            val costInUsd: Int?,
            val isAvailableToPurchase: Boolean,
        )
    }

    object NftDetails : RouteWithArg("NftDetails") {

        @Serializable
        data class Args(
            val type: NftType,
            val image: FullUrl, // todo why FullUrl?
            val dailyReward: Int,
        )
    }

    object WebView : Route("WebView")
}

object AppDeeplink {

    private val BaseUri = "https://snapsapp.io".toUri()

    private fun pathProfile() = "${BaseUri}blogger"

    fun parse(deeplink: String?): Deeplink? {
        val result = when {
            deeplink == null -> null
            deeplink.startsWith(pathProfile()) -> deeplink.removePrefix("${pathProfile()}/").let(::Profile)
            else -> null
        }
        log("Deep link parsed: $deeplink into $result")
        return result
    }

    fun generateSharingLink(deeplink: Deeplink): String {
        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link = Uri.parse(deeplink.path())
            domainUriPrefix = BaseUri.toString()
            androidParameters {
                build()
            }
            buildDynamicLink()
        }
        return dynamicLink.uri.toString()
    }

    data class Profile(val userId: Uuid) : Deeplink(pathProfile()) {

        override val pattern = "$value/{$DefaultArgKey}"
        override fun path() = "$value/%s".format(userId)
    }
}