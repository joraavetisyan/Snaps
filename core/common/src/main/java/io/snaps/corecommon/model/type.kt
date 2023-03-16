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
    @SerialName("Free") Free(0),
    @SerialName("Newbee") Newbie(1),
    @SerialName("Viewer") Viewer(2),
    @SerialName("Follower") Follower(3),
    @SerialName("Sub") Sub(4),
    @SerialName("Sponsor") Sponsor(5),
    @SerialName("Influencer") Influencer(6),
    @SerialName("FamousGuy") FamousGuy(7),
    @SerialName("Star") Star(8),
    @SerialName("Rockstar") Rockstar(9),
    @SerialName("SuperStar") SuperStar(10),
    @SerialName("Legend") Legend(11),
}