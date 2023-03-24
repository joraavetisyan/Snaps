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

// todo store ids
@Serializable
enum class NftType(val intType: Int, val storeId: String?) {
    @SerialName("Free") Free(0, null),
    @SerialName("Newbee") Newbie(1, "iap_newbie_test"),
    @SerialName("Viewer") Viewer(2, null),
    @SerialName("Follower") Follower(3, null),
    @SerialName("Sub") Sub(4, null),
    @SerialName("Sponsor") Sponsor(5, null),
    @SerialName("Influencer") Influencer(6, null),
    @SerialName("FamousGuy") FamousGuy(7, null),
    @SerialName("Star") Star(8, null),
    @SerialName("Rockstar") Rockstar(9, null),
    @SerialName("SuperStar") SuperStar(10, null),
    @SerialName("Legend") Legend(11, null),
}