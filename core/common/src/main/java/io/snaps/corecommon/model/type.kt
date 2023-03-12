package io.snaps.corecommon.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime
import java.util.UUID
import java.util.Currency

typealias DateTime = String
typealias Timestamp = Long
typealias Token = String
typealias FullUrl = String
typealias DeviceId = String
typealias Uuid = String
typealias CurrencySymbol = String
typealias WalletAddress = String
typealias NftTypeInt = Int

fun generateCurrentDateTime() = ZonedDateTime.now().toString()
fun generateRequestId() = UUID.randomUUID().toString()

@Serializable
enum class FiatCurrency(
    val symbol: CurrencySymbol,
) {
    @SerialName("643") RUB("₽"),
    @SerialName("840") USD("$"),
    @SerialName("978") EUR("€"),
    ;

    val displayName
        get() = runCatching {
            Currency.getInstance(name).displayName
        }.getOrNull().orEmpty()
}

@Serializable
enum class SubsType {
    Subscriptions, Subscribers,
}

@Serializable
data class WalletModel(
    @SerialName("coinUid") val coinUid: String,
    @SerialName("receiveAddress") val receiveAddress: WalletAddress,
    @SerialName("symbol") val symbol: String,
    @SerialName("iconUrl") val iconUrl: FullUrl,
    @SerialName("coinValue") val coinValue: String,
    @SerialName("fiatValue") val fiatValue: String,
)

@Serializable
enum class SocialNetwork(
    val url: String
) {
    Instagram("com.instagram.android"),
    Facebook("com.facebook.katana"),
    Twitter("com.twitter.android"),
}

@Serializable
enum class QuestType {
    Like,
    PublishVideo,
    Subscribe,
    Watch,
    SocialShare,
    SocialPost,
}

@Serializable
enum class NftType(val intType: Int) {
    Free(0),
    Newbee(1),
    Viewer(2),
    Follower(3),
    Sub(4),
    Sponsor(5),
    Influencer(6),
    FamousGuy(7),
    Star(8),
    Rockstar(9),
    SuperStar(10),
    Legend(11),
}