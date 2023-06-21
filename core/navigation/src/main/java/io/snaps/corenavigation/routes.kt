package io.snaps.corenavigation

import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.CoinType
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.MysteryBoxType
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.SubsType
import io.snaps.corecommon.model.TaskType
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

    override fun path(vararg args: Any): String = "$value?$DefaultArgKey=${args.firstOrNull()?.toString()?.let(Uri::encode)}"
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

    object SingleVideo : RouteWithArg("SingleVideo") {

        @Serializable
        data class Args(
            val videoClipId: Uuid,
        )
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
            val coin: CoinType,
        )
    }

    object WithdrawSnaps : Route("WithdrawSnaps")

    object Exchange : RouteWithArg("Exchange") {

        @Serializable
        data class Args(
            val coin: CoinType,
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
            val subsType: SubsType = SubsType.Subscriptions,
            val userName: String,
            val totalSubscriptions: Int,
            val totalSubscribers: Int,
        )
    }

    object FindPoints : Route("FindPoints")

    object ShareTemplate : Route("ShareTemplate")

    object TaskDetails : RouteWithArg("TaskDetails") {

        @Serializable
        data class Args(
            val type: TaskType,
            val energy: Int,
            val count: Int?,
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

    object UserFeed : RouteWithArg("UserFeed") {

        @Serializable
        data class Args(
            val userId: Uuid?,
            val position: Int,
        )
    }

    object LikedFeed : RouteWithArg("LikedFeed") {

        @Serializable
        data class Args(
            val userId: Uuid?,
            val position: Int,
        )
    }

    object RankSelection : Route("RankSelection")

    object Purchase : RouteWithArg("Purchase") {

        @Serializable
        data class Args(
            val type: NftType,
        )
    }

    object UserNftDetails : RouteWithArg("UserNftDetails") {

        @Serializable
        data class Args(
            val nftId: Uuid,
        )
    }

    object WebView : RouteWithArg("WebView") {

        @Serializable
        data class Args(
            val url: FullUrl,
        )
    }

    object ConnectInstagram : Route("ConnectInstagram")

    object EditProfile : Route("EditProfile")

    object EditName : Route("EditName")

    object AboutProject : Route("AboutProject")

    object MysteryBox : RouteWithArg("MysteryBox") {

        @Serializable
        data class Args(
            val type: MysteryBoxType,
        )
    }
}

object AppDeeplink {

    private val BaseUri = "https://snapsapp.io".toUri()

    private val pathProfile = "$BaseUri/blogger?id="
    private val pathVideoClip = "$BaseUri/video?id="
    private val pathInvite = "$BaseUri/invite?code="

    fun parse(deeplink: String?): Deeplink? {
        val result = when {
            deeplink == null -> null
            deeplink.startsWith(pathProfile) -> deeplink.removePrefix(pathProfile).let(::Profile)
            deeplink.startsWith(pathVideoClip) -> deeplink.removePrefix(pathVideoClip).let(::VideoClip)
            deeplink.startsWith(pathInvite) -> deeplink.removePrefix(pathInvite).let(::Invite)
            else -> null
        }
        log("Deep link parsed: $deeplink into $result")
        return result
    }

    fun generateSharingLink(deeplink: Deeplink): String {
        // When firebase dynamic links support is added, use this
        /*val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link = Uri.parse(deeplink.path())
            domainUriPrefix = BaseUri.toString()
            androidParameters { build() }
            buildDynamicLink()
        }
        return dynamicLink.uri.toString()*/
        return deeplink.path()
    }

    data class Profile(val id: Uuid) : Deeplink(pathProfile) {

        override fun path() = "$value%s".format(id)
    }

    data class VideoClip(val id: Uuid) : Deeplink(pathVideoClip) {

        override fun path() = "$value%s".format(id)
    }

    data class Invite(val code: String) : Deeplink(pathInvite) {

        override fun path() = "$value%s".format(code)
    }
}